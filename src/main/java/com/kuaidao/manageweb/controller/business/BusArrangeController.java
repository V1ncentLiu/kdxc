package com.kuaidao.manageweb.controller.business;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kuaidao.aggregation.dto.project.ProjectInfoPageParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.aggregation.dto.clue.BusArrangeDTO;
import com.kuaidao.aggregation.dto.clue.BusArrangeParam;
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.area.SysRegionFeignClient;
import com.kuaidao.manageweb.feign.clue.BusArrangeFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.area.SysRegionDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

/**
 * 商务排班Controller
 */
@Controller
@RequestMapping("/business/arrange")
public class BusArrangeController {
    private static Logger logger = LoggerFactory.getLogger(BusArrangeController.class);
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;
    @Autowired
    SysRegionFeignClient sysRegionFeignClient;
    @Autowired
    private BusArrangeFeignClient busArrangeFeignClient;
    @Autowired
    UserInfoFeignClient userInfoFeignClient;

    /**
     * 商务排班表页面初始化
     * 
     * @param request
     * @return
     */
    @RequiresPermissions("business:busArrangePage:view")
    @RequestMapping("/busArrangePage")
    public String busArrangePage(HttpServletRequest request) {
        String ownOrgId = "";
        UserInfoDTO user = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = user.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
        orgDto.setOrgType(OrgTypeConstant.SWZ);
        orgDto.setSystemCode(SystemCodeConstant.HUI_JU);
        if (user.getBusinessLine() != null) {
            orgDto.setBusinessLine(user.getBusinessLine());
        }
        // 电销小组
        List<OrganizationRespDTO> dxList = getTeleGroupByBusinessLine(user.getBusinessLine());

        // 查询项目列表
        ProjectInfoPageParam param = new ProjectInfoPageParam();
        param.setIsNotSign(-1);
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.listNoPage(param);
        // 获取省份
        List<SysRegionDTO> proviceslist = sysRegionFeignClient.getproviceList().getData();

        // 商务小组
        if (RoleCodeEnum.SWZJ.name().equals(roleInfoDTO.getRoleCode())) {
            List<OrganizationDTO> swList = getCurTeleGroupList(user.getOrgId());
            request.setAttribute("swList", swList);
            ownOrgId = String.valueOf(user.getOrgId());
            request.setAttribute("ownOrgId", ownOrgId);
        } else {
            List<OrganizationDTO> swList = getTeleGroupByRoleCode(user);
            request.setAttribute("swList", swList);
        }
        request.setAttribute("dxList", dxList);
        request.setAttribute("projectList", allProject.getData());
        request.setAttribute("provinceList", proviceslist);
        return "business/busArrangePage";
    }

    /***
     * 商务排班表列表
     *
     * @return
     */
    @PostMapping("/arrangeList")
    @ResponseBody
    @RequiresPermissions("business:busArrangePage:view")
    public JSONResult<PageBean<BusArrangeDTO>> arrangeList(@RequestBody BusArrangeParam pageParam,
            HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        pageParam.setUserId(user.getId());
        List<RoleInfoDTO> roleList = user.getRoleList();
        String roleCode = roleList.get(0).getRoleCode();
        Long orgId = user.getOrgId();
        if (RoleCodeEnum.SWDQZJ.name().equals(roleCode)
                || RoleCodeEnum.SWZXWY.name().equals(roleCode)
                || RoleCodeEnum.SWZC.name().equals(roleCode)) {
            UserOrgRoleReq req = new UserOrgRoleReq();
            req.setOrgId(orgId);
            req.setRoleCode(RoleCodeEnum.SWZJ.name());
            JSONResult<List<UserInfoDTO>> userJr = userInfoFeignClient.listByOrgAndRole(req);
            List<UserInfoDTO> userInfoDTOList = userJr.getData();
            if (userInfoDTOList != null && userInfoDTOList.size() != 0) {
                List<Long> idList = userInfoDTOList.stream().map(UserInfoDTO::getId)
                        .collect(Collectors.toList());
                pageParam.setBusDirectorIdList(idList);
            }
        } else if (RoleCodeEnum.SWZJ.name().equals(roleCode)) {// 商务总监
            List<Long> idList = new ArrayList<>();
            idList.add(user.getId());
            pageParam.setBusDirectorIdList(idList);
        } else {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(), "角色没有权限");
        }
        JSONResult<PageBean<BusArrangeDTO>> busArrangeList =
                busArrangeFeignClient.arrangeList(pageParam);
        return busArrangeList;
    }

    /**
     * 导出
     *
     * @param reqDTO
     * @return
     */
    @RequiresPermissions("business:busArrangePage:export")
    @PostMapping("/exportBusArrange")
    @LogRecord(description = "商务排班表导出", operationType = OperationType.EXPORT,
            menuName = MenuEnum.TRUCKING_ORDER_PAGE)
    public void exportBusArrange(@RequestBody BusArrangeParam reqDTO, HttpServletResponse response)
            throws Exception {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long orgId = curLoginUser.getOrgId();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        if (roleList != null && roleList.size() != 0) {
            RoleInfoDTO roleInfoDTO = roleList.get(0);
            String roleCode = roleInfoDTO.getRoleCode();
            if (RoleCodeEnum.SWDQZJ.name().equals(roleCode)
                    || RoleCodeEnum.SWZXWY.name().equals(roleCode)) {
                UserOrgRoleReq req = new UserOrgRoleReq();
                req.setOrgId(orgId);
                req.setRoleCode(RoleCodeEnum.SWZJ.name());
                JSONResult<List<UserInfoDTO>> userJr = userInfoFeignClient.listByOrgAndRole(req);
                if (userJr == null || !JSONResult.SUCCESS.equals(userJr.getCode())) {
                    logger.error(
                            "商务排班表-userInfoFeignClient.listByOrgAndRole(req),param{{}},res{{}}",
                            req, userJr);
                }
                List<UserInfoDTO> userInfoDTOList = userJr.getData();
                if (userInfoDTOList != null && userInfoDTOList.size() != 0) {
                    List<Long> idList = userInfoDTOList.stream().map(UserInfoDTO::getId)
                            .collect(Collectors.toList());
                    reqDTO.setBusDirectorIdList(idList);
                }

            } else if (RoleCodeEnum.SWZJ.name().equals(roleCode)) {// 商务总监
                List<Long> idList = new ArrayList<>();
                idList.add(curLoginUser.getId());
                reqDTO.setBusDirectorIdList(idList);
            }
        }

        JSONResult<List<BusArrangeDTO>> busArrangeListJson =
                busArrangeFeignClient.exportBusArrangeList(reqDTO);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getHeadTitleList());

        if (JSONResult.SUCCESS.equals(busArrangeListJson.getCode())
                && busArrangeListJson.getData() != null
                && busArrangeListJson.getData().size() != 0) {
            List<ProjectInfoDTO> allProject = getAllProjectList();
            Map<Long, List<ProjectInfoDTO>> proMap = allProject.parallelStream()
                    .collect(Collectors.groupingBy(ProjectInfoDTO::getId));
            List<BusArrangeDTO> arrangeList = busArrangeListJson.getData();
            int size = arrangeList.size();
            for (int i = 0; i < size; i++) {
                BusArrangeDTO busArrangeDTO = arrangeList.get(i);
                List<Object> curList = new ArrayList<>();
                curList.add(i + 1);
                curList.add(getTimeStr(busArrangeDTO.getReserveTime()));
                curList.add(busArrangeDTO.getCusName());
                curList.add(busArrangeDTO.getSignProvince());
                curList.add(busArrangeDTO.getSignCity());
                curList.add(busArrangeDTO.getSignDictrict());
                curList.add(busArrangeDTO.getTeleSaleName());
                curList.add(busArrangeDTO.getTasteProjectName());
                curList.add(busArrangeDTO.getCompanyName());
                curList.add(busArrangeDTO.getTeleGorupName());
                if (StringUtils.isNotBlank(busArrangeDTO.getTeleProjectId())) {
                    curList.add(transFormTeleProjectName(proMap, busArrangeDTO.getTeleProjectId()));
                } else {
                    curList.add("");
                }
                // curList.add(busArrangeDTO.getTeleProjectName());
                curList.add(busArrangeDTO.getTeleDirectorName());
                if (busArrangeDTO.getIsAllocate() == 1) {
                    curList.add(busArrangeDTO.getBusSaleName());
                } else {
                    curList.add("");
                }

                dataList.add(curList);
            }

        } else {
            logger.error("export bussiness_arrange param{{}},res{{}}", reqDTO, busArrangeListJson);
        }

        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel1(dataList);


        String name = "商务排班表" + DateUtil.convert2String(new Date(), DateUtil.ymdhms2) + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();

    }

    /**
     * 商务排班表表头
     * 
     * @return
     */
    private List<Object> getHeadTitleList() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("来访日期");
        headTitleList.add("客户姓名");
        headTitleList.add("签约省份");
        headTitleList.add("签约城市");
        headTitleList.add("签约区/县");
        headTitleList.add("电销顾问");
        headTitleList.add("签约项目");
        headTitleList.add("所属公司");
        headTitleList.add("电销组");
        headTitleList.add("电销项目");
        headTitleList.add("电销组总监");
        headTitleList.add("商务经理");
        headTitleList.add("是否到访");
        headTitleList.add("洽谈结果");
        headTitleList.add("备注");
        return headTitleList;
    }

    /**
     * 格式化时间
     * 
     * @param date
     * @return
     */
    private String getTimeStr(Date date) {
        if (date == null) {
            return "";
        }
        return DateUtil.convert2String(date, DateUtil.ymdhms);
    }

    /**
     * 获取电销项目项目名称
     * 
     * @param proMap
     * @param teleProjectIds
     * @return
     */
    private String transFormTeleProjectName(Map<Long, List<ProjectInfoDTO>> proMap,
            String teleProjectIds) {
        String[] idArr = teleProjectIds.split(",", -1);
        StringBuilder projectName = new StringBuilder();
        String name = "";
        for (int i = 0; i < idArr.length; i++) {
            if (proMap.containsKey(Long.valueOf(idArr[i]))) {
                projectName
                        .append(proMap.get(Long.valueOf(idArr[i])).get(0).getProjectName() + ",");
            }
        }
        name = projectName.toString();
        if (name.length() > 0) {
            return name.substring(0, name.length() - 1);
        } else {
            return "";
        }

    }

    /**
     * 获取所有项目
     * 
     * @return
     */
    private List<ProjectInfoDTO> getAllProjectList() {
        JSONResult<List<ProjectInfoDTO>> projectInfoJr = projectInfoFeignClient.allProject();
        return projectInfoJr.getData();
    }

    /**
     * 根据角色获取对应角色下的商务组
     * 
     * @param user
     * @return
     */
    private List<OrganizationDTO> getTeleGroupByRoleCode(UserInfoDTO user) {
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList == null) {
            return null;
        }
        if (RoleCodeEnum.SWDQZJ.name().equals(roleList.get(0).getRoleCode())
                || RoleCodeEnum.SWZC.name().equals(roleList.get(0).getRoleCode())
                || RoleCodeEnum.SWZXWY.name().equals(roleList.get(0).getRoleCode())) {
            // 如果是商务大区总监大区下所有商务组
            Long orgId = user.getOrgId();
            OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
            organizationQueryDTO.setParentId(orgId);
            organizationQueryDTO.setOrgType(OrgTypeConstant.SWZ);
            // 查询下级商务组(查询使用)
            JSONResult<List<OrganizationDTO>> listDescenDantByParentId =
                    organizationFeignClient.listDescenDantByParentId(organizationQueryDTO);
            List<OrganizationDTO> data = listDescenDantByParentId.getData();
            return data;
        } else if (roleList != null
                && RoleCodeEnum.SWZJL.name().equals(roleList.get(0).getRoleCode())) {
            // 如果是电销副总展现事业部下所有组
            Long orgId = user.getOrgId();
            OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
            organizationQueryDTO.setParentId(orgId);
            organizationQueryDTO.setOrgType(OrgTypeConstant.SWZ);
            // 查询下级商务组(查询使用)
            JSONResult<List<OrganizationDTO>> listDescenDantByParentId =
                    organizationFeignClient.listDescenDantByParentId(organizationQueryDTO);
            List<OrganizationDTO> data = listDescenDantByParentId.getData();
            return data;
        }
        return null;
    }

    /**
     * 获取该业务下 的所有电销组
     *
     * @param businessLine
     * @return
     */
    private List<OrganizationRespDTO> getTeleGroupByBusinessLine(Integer businessLine) {
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        queryDTO.setBusinessLine(businessLine);
        queryDTO.setOrgType(OrgTypeConstant.DXZ);
        JSONResult<List<OrganizationRespDTO>> orgGroupJr =
                organizationFeignClient.queryOrgByParam(queryDTO);
        if (!JSONResult.SUCCESS.equals(orgGroupJr.getCode())) {
            logger.error("getCurOrgGroupByOrgId,param{{}},res{{}}", queryDTO, orgGroupJr);
            return new ArrayList<>();
        }
        return orgGroupJr.getData();
    }

    /**
     * 获取商务总监的商务组
     * 
     * @param orgId
     * @return
     */
    private List<OrganizationDTO> getCurTeleGroupList(Long orgId) {
        OrganizationDTO curOrgGroupByOrgId = getCurOrgGroupByOrgId(String.valueOf(orgId));
        List<OrganizationDTO> swGroupIdList = new ArrayList<>();
        swGroupIdList.add(curOrgGroupByOrgId);
        return swGroupIdList;
    }

    /**
     * 获取当前 orgId所在的组织
     *
     * @param orgId
     * @param
     * @return
     */
    private OrganizationDTO getCurOrgGroupByOrgId(String orgId) {
        // 电销组
        IdEntity idEntity = new IdEntity();
        idEntity.setId(orgId);
        JSONResult<OrganizationDTO> orgJr = organizationFeignClient.queryOrgById(idEntity);
        if (!JSONResult.SUCCESS.equals(orgJr.getCode())) {
            logger.error("getCurOrgGroupByOrgId,param{{}},res{{}}", idEntity, orgJr);
            return null;
        }
        return orgJr.getData();
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
