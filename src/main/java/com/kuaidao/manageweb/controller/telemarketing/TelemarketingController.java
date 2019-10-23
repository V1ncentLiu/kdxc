package com.kuaidao.manageweb.controller.telemarketing;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
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
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.aggregation.dto.telemarkting.TelemarketingLayoutDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.telemarketing.TelemarketingLayoutFeignClient;
import com.kuaidao.manageweb.util.IdUtil;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;

/**
 * @Auther: admin
 * @Date: 2019/1/2 15:14
 * @Description: 电销管理
 */

@Controller
@RequestMapping("/telemarketing")
public class TelemarketingController {

    private static Logger logger = LoggerFactory.getLogger(TelemarketingController.class);
    @Autowired
    TelemarketingLayoutFeignClient telemarketingLayoutFeignClient;

    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;

    @Autowired
    private OrganizationFeignClient organizationFeignClient;

    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;

    /**
     * 电销布局列表
     * 
     * @return
     */
    @RequestMapping("/telemarketingLayoutList")
    public String inviteAreaList(HttpServletRequest request) {
        // 查询项目列表
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();

        OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
        orgDto.setOrgType(OrgTypeConstant.DXZ);
        orgDto.setSystemCode(SystemCodeConstant.HUI_JU);
        // 商务小组
        JSONResult<List<OrganizationRespDTO>> dzList =
                organizationFeignClient.queryOrgByParam(orgDto);
        List<DictionaryItemRespDTO> categoryList = getDictionaryByCode("clueCategory");
        request.setAttribute("categoryList", categoryList);
        request.setAttribute("dzList", dzList.getData());
        request.setAttribute("projectList", allProject.getData());
        return "telemarketing/telemarketingLayoutList";
    }

    /**
     * 电销布局列表
     * 
     * @return
     */
    @RequestMapping("/getTelemarketingLayoutList")
    @ResponseBody
    public JSONResult<PageBean<TelemarketingLayoutDTO>> getTelemarketingLayoutList(
            HttpServletRequest request,
            @RequestBody TelemarketingLayoutDTO telemarketingLayoutDTO) {
        return telemarketingLayoutFeignClient.getTelemarketingLayoutList(telemarketingLayoutDTO);
    }

    /**
     * 根据id查询电销布局
     * 
     * @return
     */
    @RequestMapping("/findTelemarketingById")
    @ResponseBody
    public JSONResult<TelemarketingLayoutDTO> findTelemarketingById(HttpServletRequest request,
            @RequestBody TelemarketingLayoutDTO telemarketingLayoutDTO) {
        return telemarketingLayoutFeignClient.findTelemarketingById(telemarketingLayoutDTO);
    }

    /**
     * 添加电销布局
     * 
     * @return
     */
    @RequestMapping("/addTelemarketingLayout")
    @LogRecord(description = "添加电销布局", operationType = LogRecord.OperationType.INSERT,
            menuName = MenuEnum.TELEMARKTINGLAYOUT)
    @ResponseBody
    public JSONResult addTelemarketingLayout(
            @RequestBody TelemarketingLayoutDTO telemarketingLayoutDTO) {
        UserInfoDTO user =
                (UserInfoDTO) SecurityUtils.getSubject().getSession().getAttribute("user");
        telemarketingLayoutDTO.setCreateUser(user.getId());
        return telemarketingLayoutFeignClient
                .addOrUpdateTelemarketingLayout(telemarketingLayoutDTO);
    }

    /**
     * 修改电销布局
     * 
     * @return
     */
    @RequestMapping("/updateTelemarketingLayout")
    @LogRecord(description = "修改电销布局", operationType = LogRecord.OperationType.UPDATE,
            menuName = MenuEnum.TELEMARKTINGLAYOUT)
    @ResponseBody
    public JSONResult updateTelemarketingLayout(
            @RequestBody TelemarketingLayoutDTO telemarketingLayoutDTO) {
        return telemarketingLayoutFeignClient
                .addOrUpdateTelemarketingLayout(telemarketingLayoutDTO);
    }

    /**
     * 删除电销布局
     * 
     * @return
     */
    @RequestMapping("/deleTelemarketingLayout")
    @LogRecord(description = "删除电销布局", operationType = LogRecord.OperationType.DELETE,
            menuName = MenuEnum.TELEMARKTINGLAYOUT)
    @ResponseBody
    public JSONResult deleTelemarketingLayout(
            @RequestBody TelemarketingLayoutDTO telemarketingLayoutDTO) {
        return telemarketingLayoutFeignClient.deleTelemarketingLayout(telemarketingLayoutDTO);
    }

    /**
     * 预览
     * 
     * @param
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
        List<TelemarketingLayoutDTO> dataList = new ArrayList<TelemarketingLayoutDTO>();

        for (int i = 1; i < excelDataList.size(); i++) {
            List<Object> rowList = excelDataList.get(i);
            TelemarketingLayoutDTO rowDto = new TelemarketingLayoutDTO();
            for (int j = 0; j < rowList.size(); j++) {
                Object object = rowList.get(j);
                String value = (String) object;
                if (j == 0) {// 序号
                    rowDto.setSerialNumber(value);
                } else if (j == 1) {// 商务小组
                    rowDto.setTelemarketingTeam(value);
                } else if (j == 2) {// 区域
                    rowDto.setProjects(value);
                } else if (j == 3) {// 电销组
                    rowDto.setBeginTime(value);
                } else if (j == 4) {// 签约项目
                    rowDto.setEndTime(value);
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
     * 导入电销布局
     * 
     * @return
     * @throws Exception
     */
    @RequestMapping("/importInvitearea")
    @LogRecord(description = "导入电销布局", operationType = LogRecord.OperationType.IMPORTS,
            menuName = MenuEnum.TELEMARKTINGLAYOUT)
    @ResponseBody
    public JSONResult importInvitearea(@RequestBody TelemarketingLayoutDTO telemarketingLayoutDTO)
            throws Exception {
        UserInfoDTO user =
                (UserInfoDTO) SecurityUtils.getSubject().getSession().getAttribute("user");
        List<TelemarketingLayoutDTO> list = telemarketingLayoutDTO.getList();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        // 存放合法的数据
        List<TelemarketingLayoutDTO> dataList = new ArrayList<TelemarketingLayoutDTO>();
        // 存放非法的数据
        List<TelemarketingLayoutDTO> illegalDataList = new ArrayList<TelemarketingLayoutDTO>();
        // 获取省份
        OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
        orgDto.setOrgType(OrgTypeConstant.DXZ);
        // 电销小组
        JSONResult<List<OrganizationRespDTO>> dxList =
                organizationFeignClient.queryOrgByParam(orgDto);
        // 查询项目列表
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();


        if (list != null && list.size() > 0) {

            for (TelemarketingLayoutDTO telemarketingLayoutDTO2 : list) {
                boolean islegal = true;// true合法 false不合法
                String projectIds = "";
                if (islegal && telemarketingLayoutDTO2.getTelemarketingTeam() != null) {
                    islegal = false;
                    for (OrganizationRespDTO organizationRespDTO : dxList.getData()) {
                        if ((organizationRespDTO.getName().trim())
                                .equals(telemarketingLayoutDTO2.getTelemarketingTeam().trim())) {
                            telemarketingLayoutDTO2
                                    .setTelemarketingTeamId(organizationRespDTO.getId());
                            islegal = true;
                            break;
                        }
                    }
                } else {
                    islegal = false;
                }

                if (islegal && telemarketingLayoutDTO2.getProjects() != null) {
                    String[] projects = telemarketingLayoutDTO2.getProjects().split(",");
                    for (int i = 0; i < projects.length; i++) {
                        int isCanUser = 1;// 是否能用0 可用 1不可用
                        for (ProjectInfoDTO projectInfoDTO : allProject.getData()) {
                            if ((projectInfoDTO.getProjectName().trim())
                                    .equals(projects[i].trim())) {
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

                if (islegal && (telemarketingLayoutDTO2.getBeginTime() == null
                        || "".equals(telemarketingLayoutDTO2.getBeginTime())
                        || "".equals(telemarketingLayoutDTO2.getEndTime())
                        || islegal && telemarketingLayoutDTO2.getEndTime() == null)) {
                    islegal = false;
                } else if (islegal && format.parse(telemarketingLayoutDTO2.getBeginTime())
                        .getTime() > format.parse(
                                telemarketingLayoutDTO2.getEndTime().substring(0, 10) + " 23:59:59")
                                .getTime()) {
                    islegal = false;
                }

                if (islegal && telemarketingLayoutDTO2.getEndTime() == null) {
                    islegal = false;
                }

                if (islegal) {
                    telemarketingLayoutDTO2.setCreateUser(user.getId());
                    telemarketingLayoutDTO2.setProjectIds(projectIds);
                    telemarketingLayoutDTO2.setCreateTime(new Date());
                    telemarketingLayoutDTO2.setId(IdUtil.getUUID());
                    dataList.add(telemarketingLayoutDTO2);
                } else {
                    illegalDataList.add(telemarketingLayoutDTO2);
                }
            }
        }
        if (dataList != null && dataList.size() > 0) {
            JSONResult jsonResult =
                    telemarketingLayoutFeignClient.addTelemarketingLayoutList(dataList);
            if (!jsonResult.getCode().equals("0")) {
                return new JSONResult<>().success(list);
            }
        }
        return new JSONResult<>().success(illegalDataList);
    }
    /**
     * 查询字典表
     *
     * @param code
     * @return
     */
    private List<DictionaryItemRespDTO> getDictionaryByCode(String code) {
        JSONResult<List<DictionaryItemRespDTO>> queryDicItemsByGroupCode =
                dictionaryItemFeignClient.queryDicItemsByGroupCode(code);
        if (queryDicItemsByGroupCode != null
                && JSONResult.SUCCESS.equals(queryDicItemsByGroupCode.getCode())) {
            return queryDicItemsByGroupCode.getData();
        }
        return null;
    }
}
