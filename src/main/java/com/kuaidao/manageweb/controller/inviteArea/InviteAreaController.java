package com.kuaidao.manageweb.controller.inviteArea;

import com.kuaidao.aggregation.dto.invitearea.InviteAreaDTO;
import com.kuaidao.businessconfig.dto.project.CompanyInfoDTO;
import com.kuaidao.businessconfig.dto.project.CompanyInfoPageParam;
import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.businessconfig.dto.project.ProjectInfoPageParam;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.area.SysRegionFeignClient;
import com.kuaidao.manageweb.feign.invitearea.InviteareaFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.CompanyInfoFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.util.IdUtil;
import com.kuaidao.sys.dto.area.SysRegionDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Auther: admin
 * @Date: 2019/1/2 15:14
 * @Description: y邀约区域
 */

@Controller
@RequestMapping("/invitearea")
public class InviteAreaController {

    private static Logger logger = LoggerFactory.getLogger(InviteAreaController.class);
    @Autowired
    InviteareaFeignClient inviteareaFeignClient;

    @Autowired
    SysRegionFeignClient sysRegionFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;
    @Autowired
    private CompanyInfoFeignClient companyInfoFeignClient;




    /**
     * 邀约记录列表页面
     * 
     * @return
     */
    @RequestMapping("/inviteAreaList")
    public String inviteAreaList(HttpServletRequest request) {
        OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
        orgDto.setOrgType(OrgTypeConstant.SWZ);
        orgDto.setSystemCode(SystemCodeConstant.HUI_JU);
        // 商务小组
        JSONResult<List<OrganizationRespDTO>> swList =
                organizationFeignClient.queryOrgByParam(orgDto);
        orgDto.setOrgType(OrgTypeConstant.DXZ);
        // 电销小组
        JSONResult<List<OrganizationRespDTO>> dxList =
                organizationFeignClient.queryOrgByParam(orgDto);
        request.setAttribute("swList", swList.getData());
        request.setAttribute("dxList", dxList.getData());
        return "inviteArea/inviteAreaList";
    }

    /**
     * 邀约记录列表
     * 
     * @return
     */
    @RequestMapping("/inviteAreaListPage")
    @ResponseBody
    public JSONResult<PageBean<InviteAreaDTO>> inviteAreaListPage(HttpServletRequest request,
            @RequestBody InviteAreaDTO inviteAreaDTO) {
        return inviteareaFeignClient.inviteAreaListPage(inviteAreaDTO);
    }

    /**
     * 添加邀约区域页面
     * 
     * @return
     */
    @RequestMapping("/addInviteAreaPage")
    public String addInviteArea(HttpServletRequest request) {
        UserInfoDTO userInfoDTO = getUser();
        // 获取省份
        List<SysRegionDTO> proviceslist = sysRegionFeignClient.getproviceList().getData();
        OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
        orgDto.setOrgType(OrgTypeConstant.SWZ);
        orgDto.setSystemCode(SystemCodeConstant.HUI_JU);
//        if(userInfoDTO.getBusinessLine() != null){
//            orgDto.setBusinessLine(userInfoDTO.getBusinessLine());
//        }
        // 商务小组
        JSONResult<List<OrganizationRespDTO>> swList =
                organizationFeignClient.queryOrgByParam(orgDto);
        orgDto.setOrgType(OrgTypeConstant.DXZ);
        // 电销小组
        JSONResult<List<OrganizationRespDTO>> dxList =
                organizationFeignClient.queryOrgByParam(orgDto);
        // 查询项目列表
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();
        List<ProjectInfoDTO> projectInfoDTOList = allProject.getData();
        //
        SysRegionDTO sysRegionDTO = new SysRegionDTO();
        sysRegionDTO.setId(0L);
        sysRegionDTO.setName("全选");
        proviceslist.add(0, sysRegionDTO);
        request.setAttribute("projectList", allProject.getData());
        request.setAttribute("swList", swList.getData());
        request.setAttribute("dxList", dxList.getData());
        request.setAttribute("proviceslist", proviceslist);
        return "inviteArea/addInviteAreaPage";
    }

    /**
     * 根据业务线获取电销组
     * @param inviteAreaDTO
     * @return
     */
    @PostMapping("/getDxOrganizations")
    @ResponseBody
    public JSONResult<List<OrganizationRespDTO>> getDxOrganizations(@RequestBody InviteAreaDTO inviteAreaDTO){
        Integer businessLine = inviteAreaDTO.getBusinessLine();
        OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
        orgDto.setOrgType(OrgTypeConstant.DXZ);
        orgDto.setSystemCode(SystemCodeConstant.HUI_JU);
        orgDto.setBusinessLine(businessLine);
        // 电销小组
        JSONResult<List<OrganizationRespDTO>> dxList =
            organizationFeignClient.queryOrgByParam(orgDto);
        return dxList;
    }
    /**
     * 修改邀约区域页面
     * 
     * @return
     */
    @RequestMapping("/updateInviteAreaPage")
    public String updateInviteAreaPage(HttpServletRequest request) {
        String ids = request.getParameter("ids");
        InviteAreaDTO inviteAreaDTO = new InviteAreaDTO();
        inviteAreaDTO.setIds(ids);
        // 获取省份
        List<SysRegionDTO> proviceslist = sysRegionFeignClient.getproviceList().getData();
        OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
        orgDto.setOrgType(OrgTypeConstant.SWZ);
        orgDto.setSystemCode(SystemCodeConstant.HUI_JU);
        // 查询项目列表
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();

        List<InviteAreaDTO> inviteAreaDTOs =
                inviteareaFeignClient.getInviteAreaListByIds(inviteAreaDTO).getData();
//        if(inviteAreaDTOs.get(0).getBusinessLine() != null){
//            orgDto.setBusinessLine(inviteAreaDTOs.get(0).getBusinessLine());
//        }
        // 商务小组
        JSONResult<List<OrganizationRespDTO>> swList =
            organizationFeignClient.queryOrgByParam(orgDto);
        orgDto.setOrgType(OrgTypeConstant.DXZ);
        if(inviteAreaDTOs.get(0).getBusinessLine() != null){
            orgDto.setBusinessLine(inviteAreaDTOs.get(0).getBusinessLine());
        }
        // 电销小组
        JSONResult<List<OrganizationRespDTO>> dxList =
            organizationFeignClient.queryOrgByParam(orgDto);
        int isshowBusinessGroup = 0;
        int isshowTelemarketingTeam = 0;
        int isshowprovince = 0;
        int isshowproject = 0;
        for (InviteAreaDTO inviteAreaDTO2 : inviteAreaDTOs) {
            String[] projectids = inviteAreaDTO2.getProjectIds().split(",");
            Arrays.sort(projectids);
            String[] provinces = inviteAreaDTO2.getProvincesIds().split(",");
            Arrays.sort(provinces);

            String telemarketingTeamIds = inviteAreaDTO2.getTelemarketingTeamIds();
            String[] teleTeamIds = telemarketingTeamIds.split(",");
            Arrays.sort(teleTeamIds);

            for (InviteAreaDTO inviteAreaDTO3 : inviteAreaDTOs) {
                if (inviteAreaDTO2.getId().longValue() != inviteAreaDTO3.getId().longValue()) {
                    if (inviteAreaDTO2.getBusinessGroupId().longValue() != inviteAreaDTO3
                            .getBusinessGroupId().longValue()) {
                        isshowBusinessGroup = 1;
                    }
//                    if (inviteAreaDTO2.getTelemarketingTeamId().longValue() != inviteAreaDTO3
//                            .getTelemarketingTeamId().longValue()) {
//                        isshowTelemarketingTeam = 1;
//                    }

                    String[] teleTeamIdsnew = inviteAreaDTO2.getTelemarketingTeamIds().split(",");
                    Arrays.sort(teleTeamIdsnew);
                    String[] projectidsnew = inviteAreaDTO3.getProjectIds().split(",");
                    Arrays.sort(projectidsnew);
                    String[] provincesnew = inviteAreaDTO3.getProvincesIds().split(",");
                    Arrays.sort(provincesnew);
                    if (!Arrays.equals(teleTeamIds, teleTeamIdsnew)) {
                        isshowTelemarketingTeam = 1;
                    }
                    if (!Arrays.equals(provinces, provincesnew)) {
                        isshowprovince = 1;
                    }
                    if (!Arrays.equals(projectids, projectidsnew)) {
                        isshowproject = 1;
                    }
                }

            }
        }
        //
        SysRegionDTO sysRegionDTO = new SysRegionDTO();
        sysRegionDTO.setId(0L);
        sysRegionDTO.setName("全选");
        proviceslist.add(0, sysRegionDTO);

        request.setAttribute("checkbusinessGroupId", inviteAreaDTOs.get(0).getBusinessGroupId().toString());
        String[] split = inviteAreaDTOs.get(0).getProjectIds().split(",");
        List<ProjectInfoDTO> data = allProject.getData();
        List<String> list = new ArrayList<>();
        if(data!=null){
            for(int i =0 ; i <split.length ; i++){
                for(ProjectInfoDTO projectInfoDTO : data){
                    if(projectInfoDTO.getId().toString().equals(split[i])){
                        list.add(split[i]);
                    }
                }
            }
        }

        request.setAttribute("checkTelemarketingTeam", inviteAreaDTOs.get(0).getTelemarketingTeamIds().split(","));
        request.setAttribute("checkProject", list);
        request.setAttribute("checkProvince", inviteAreaDTOs.get(0).getProvincesIds().split(","));
        request.setAttribute("ids", ids);
        request.setAttribute("inviteAreaDTOs", inviteAreaDTOs);
        request.setAttribute("isshowTelemarketingTeam", isshowTelemarketingTeam);
        request.setAttribute("isshowBusinessGroup", isshowBusinessGroup);
        request.setAttribute("isshowproject", isshowproject);
        request.setAttribute("isshowprovince", isshowprovince);
        request.setAttribute("inviteArea", inviteAreaDTOs == null ? null : inviteAreaDTOs.get(0));
        request.setAttribute("projectList", data);
        request.setAttribute("swList", swList.getData());
        request.setAttribute("dxList", dxList.getData());
        request.setAttribute("proviceslist", proviceslist);
        request.setAttribute("businessLine", inviteAreaDTOs.get(0).getBusinessLine());
        return "inviteArea/updateInviteAreaPage";
    }

    /**
     * 添加邀约区域
     * 
     * @return
     */
    @RequestMapping("/addInviteArea")
    @LogRecord(description = "添加邀约区域", operationType = LogRecord.OperationType.INSERT,
            menuName = MenuEnum.INVITEAREA)
    @ResponseBody
    public JSONResult addInviteAreaMes(@RequestBody InviteAreaDTO inviteAreaDTO) {
        inviteAreaDTO.setStatus(0);
        UserInfoDTO user =
                (UserInfoDTO) SecurityUtils.getSubject().getSession().getAttribute("user");
        inviteAreaDTO.setCreateUser(user.getId());
        return inviteareaFeignClient.addOrUpdateInviteArea(inviteAreaDTO);
    }

    /**
     * 修改邀约区域
     * 
     * @return
     */
    @RequestMapping("/updateInviteArea")
    @LogRecord(description = "修改邀约区域", operationType = LogRecord.OperationType.UPDATE,
            menuName = MenuEnum.INVITEAREA)
    @ResponseBody
    public JSONResult updateInviteAreaMes(@RequestBody InviteAreaDTO inviteAreaDTO) {
        return inviteareaFeignClient.addOrUpdateInviteArea(inviteAreaDTO);
    }

    /**
     * 删除邀约区域
     * 
     * @return
     */
    @RequestMapping("/deleInviteArea")
    @LogRecord(description = "删除邀约区域", operationType = LogRecord.OperationType.DELETE,
            menuName = MenuEnum.INVITEAREA)
    @ResponseBody
    public JSONResult deleInviteArea(@RequestBody InviteAreaDTO inviteAreaDTO) {
        return inviteareaFeignClient.deleInviteArea(inviteAreaDTO);
    }

    /**
     * 上传自定义字段
     *
     * @return
     */
    // @RequiresPermissions("customfield:batchSaveField")
    @PostMapping("/uploadCustomField")
    @ResponseBody
    public JSONResult uploadCustomField(@RequestParam("file") MultipartFile file) throws Exception {
        List<List<Object>> excelDataList = ExcelUtil.read2007Excel(file.getInputStream());
        logger.info("customfield upload size:{{}}", excelDataList.size());

        if (excelDataList == null || excelDataList.size() == 0) {
            return new JSONResult<>().fail(SysErrorCodeEnum.ERR_EXCLE_DATA.getCode(),
                    SysErrorCodeEnum.ERR_EXCLE_DATA.getMessage());
        }
        if (excelDataList.size() > 1000) {
            logger.error("上传自定义字段,大于1000条，条数{{}}", excelDataList.size());
            return new JSONResult<>().fail(SysErrorCodeEnum.ERR_EXCLE_OUT_SIZE.getCode(),
                    "导入数据过多，已超过1000条！");
        }

        // 存放合法的数据
        List<InviteAreaDTO> dataList = new ArrayList<InviteAreaDTO>();

        for (int i = 1; i < excelDataList.size(); i++) {
            List<Object> rowList = excelDataList.get(i);
            InviteAreaDTO rowDto = new InviteAreaDTO();
            for (int j = 0; j < rowList.size(); j++) {
                Object object = rowList.get(j);
                String value = (String) object;
                if (j == 0) {// 序号
                    rowDto.setSerialNumber(value);
                } else if (j == 1) {// 商务小组
                    rowDto.setBusinessGroup(value);
                } else if (j == 2) {// 区域
                    rowDto.setProvinces(value);
                } else if (j == 3) {// 电销组
                    rowDto.setTelemarketingTeam(value);
                } else if (j == 4) {// 签约项目
                    rowDto.setProjects(value);
                }
            } // inner foreach end
            dataList.add(rowDto);
        } // outer foreach end
        logger.info("upload custom filed, valid success num{{}}", dataList.size());
        /*
         * JSONResult uploadRs = customFieldFeignClient.saveBatchCustomField(dataList);
         * if(uploadRs==null || !JSONResult.SUCCESS.equals(uploadRs.getCode())) { return uploadRs; }
         */

        return new JSONResult<>().success(dataList);
    }

    /**
     * 导入邀约区域
     * 
     * @return
     */
    @RequestMapping("/importInvitearea")
    @LogRecord(description = "导入邀约区域", operationType = LogRecord.OperationType.IMPORTS,
            menuName = MenuEnum.INVITEAREA)
    @ResponseBody
    public JSONResult importInvitearea(@RequestBody InviteAreaDTO inviteAreaDTO) {
        List<InviteAreaDTO> list = inviteAreaDTO.getList();
        // 存放合法的数据
        List<InviteAreaDTO> dataList = new ArrayList<InviteAreaDTO>();
        // 存放非法的数据
        List<InviteAreaDTO> illegalDataList = new ArrayList<InviteAreaDTO>();
        // 获取省份
        List<SysRegionDTO> proviceslist = sysRegionFeignClient.getproviceList().getData();
        OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
        orgDto.setOrgType(OrgTypeConstant.SWZ);
        // 商务小组
        JSONResult<List<OrganizationRespDTO>> swList =
                organizationFeignClient.queryOrgByParam(orgDto);
        orgDto.setOrgType(OrgTypeConstant.DXZ);
        // 电销小组
        JSONResult<List<OrganizationRespDTO>> dxList =
                organizationFeignClient.queryOrgByParam(orgDto);
        // 查询项目列表
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();


        if (list != null && list.size() > 0) {
            for (InviteAreaDTO inviteAreaDTO2 : list) {
                boolean islegal = true;// true合法 false不合法
                String provinceIds = "";
                String projectIds = "";
                if (inviteAreaDTO2.getProvinces() != null) {
                    String[] provinces = inviteAreaDTO2.getProvinces().split(",");
                    for (int i = 0; i < provinces.length; i++) {
                        int isCanUser = 1;// 是否能用0 可用 1不可用
                        for (SysRegionDTO sysRegionDTO : proviceslist) {
                            if (sysRegionDTO.getName().equals(provinces[i].trim())) {
                                if ("".equals(provinceIds)) {
                                    provinceIds = sysRegionDTO.getId() + "";
                                } else {
                                    provinceIds = provinceIds + "," + sysRegionDTO.getId() + "";
                                }
                                isCanUser = 0;
                                break;
                            }
                        }
                        if (isCanUser == 1) {
                            islegal = false;
                            break;
                        }
                    }

                } else {
                    islegal = false;
                }
                Integer busLine = null;//标记商务组所在业务线
                if (islegal && inviteAreaDTO2.getBusinessGroup() != null) {
                    islegal = false;
                    for (OrganizationRespDTO organizationRespDTO : swList.getData()) {
                        if (organizationRespDTO.getName()
                                .equals(inviteAreaDTO2.getBusinessGroup().trim())) {
                            inviteAreaDTO2.setBusinessGroupId(organizationRespDTO.getId());
                            busLine = organizationRespDTO.getBusinessLine();
                            islegal = true;
                            break;
                        }
                    }
                } else {
                    islegal = false;
                }
                Integer telLine = null;//标记电销组所在业务线
                if (islegal && inviteAreaDTO2.getTelemarketingTeam() != null) {
                    islegal = false;
                    for (OrganizationRespDTO organizationRespDTO : dxList.getData()) {
                        if (organizationRespDTO.getName().equals(inviteAreaDTO2.getTelemarketingTeam().trim())) {
                            inviteAreaDTO2.setTelemarketingTeamId(""+organizationRespDTO.getId());
                            telLine = organizationRespDTO.getBusinessLine();
                            islegal = true;
                            break;
                        }
                    }
                } else {
                    islegal = false;
                }
                //商务组与电销组若非同一业务线则导入不成功
                if (busLine != null && telLine != null) {
                    if(Integer.compare(busLine,telLine) != 0){
                        islegal = false;
                    }
                }

                if (islegal && inviteAreaDTO2.getProjects() != null) {
                    String[] projects = inviteAreaDTO2.getProjects().split(",");
                    for (int i = 0; i < projects.length; i++) {
                        int isCanUser = 1;// 是否能用0 可用 1不可用
                        for (ProjectInfoDTO projectInfoDTO : allProject.getData()) {
                            if (projectInfoDTO.getProjectName().equals(projects[i].trim())) {
                                if ("".equals(projectIds)) {
                                    projectIds = projectInfoDTO.getId() + "";
                                } else {
                                    projectIds = projectIds + "," + projectInfoDTO.getId() + "";
                                }
                                isCanUser = 0;
                                break;
                            }
                        }
                        if (isCanUser == 1) {
                            islegal = false;
                            break;
                        }
                    }
                } else {
                    islegal = false;
                }
                if (islegal) {
                    inviteAreaDTO2.setProjectIds(projectIds);
                    inviteAreaDTO2.setCreateTime(new Date());
                    inviteAreaDTO2.setProvincesIds(provinceIds);
                    inviteAreaDTO2.setId(IdUtil.getUUID());
                    dataList.add(inviteAreaDTO2);
                } else {
                    illegalDataList.add(inviteAreaDTO2);
                }
            }
        }
        if (dataList != null && dataList.size() > 0) {
            JSONResult jsonResult = inviteareaFeignClient.addInviteAreaList(dataList);
            if (!jsonResult.getCode().equals("0")) {
                return new JSONResult<>().success(list);
            }
        }
        return new JSONResult<>().success(illegalDataList);
    }
    /**
     * 商务小组名称-->匹配公司表中公司名称-->找到公司对应集团-->通过集团找到所属项目（项目表中有所属集团）
     * 商务小组名称 能够匹配 公司表中公司名称
     * 公司表中保存了公司对应集团：（保存的是集团名称）
     * 查询集团下项目：项目中保存的是集团的ID。这里的集团是从哪里获取的。
     */
    @RequestMapping("/InviteProjects")
    @ResponseBody
    public List<ProjectInfoDTO> InviteProjects(@RequestBody InviteAreaDTO inviteAreaDTO){
        List<ProjectInfoDTO> list = new ArrayList<>();
        CompanyInfoPageParam param = new CompanyInfoPageParam();
        param.setCompanyName1(inviteAreaDTO.getBusinessGroup());
        param.setPageNum(1);
        param.setPageSize(99999);
        JSONResult<PageBean<CompanyInfoDTO>> list1 = companyInfoFeignClient.list(param);
        if(JSONResult.SUCCESS.equals(list1.getCode())){
            List<CompanyInfoDTO> data = list1.getData().getData();
            if(data!=null&&data.size()>0){
                CompanyInfoDTO dto = data.get(0);
                ProjectInfoPageParam p = new ProjectInfoPageParam();
                p.setGroupName(dto.getGroupName());
                JSONResult<List<ProjectInfoDTO>> listJSONResult = projectInfoFeignClient.listNoPage(p);
                if(JSONResult.SUCCESS.equals(listJSONResult.getCode())){
                    list = listJSONResult.getData();
                }
            }
        }
        return list;
    }
    /**
     * 获取当前登录账号
     *
     * @return
     */
    private UserInfoDTO getUser() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user;
    }
}
