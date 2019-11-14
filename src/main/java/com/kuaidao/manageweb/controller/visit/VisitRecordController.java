package com.kuaidao.manageweb.controller.visit;

import com.kuaidao.aggregation.dto.project.ProjectInfoPageParam;
import com.kuaidao.common.entity.IdEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kuaidao.aggregation.constant.AggregationConstant;
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.aggregation.dto.visitrecord.*;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.area.SysRegionFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.feign.visit.TrackingOrderFeignClient;
import com.kuaidao.manageweb.feign.visit.VisitRecordFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.area.SysRegionDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

/**
 * 訪問記錄
 * 
 * @author Chen
 * @date 2019年3月4日 下午1:40:44
 * @version V1.0
 */
@Controller
@RequestMapping("/visit/visitRecord")
public class VisitRecordController {

    private static Logger logger = LoggerFactory.getLogger(VisitRecordController.class);

    @Autowired
    TrackingOrderFeignClient trackingOrderFeignClient;

    @Autowired
    UserInfoFeignClient userInfoFeignClient;

    @Autowired
    ProjectInfoFeignClient projectInfoFeignClient;

    @Autowired
    OrganizationFeignClient organizationFeignClient;

    @Autowired
    SysRegionFeignClient sysRegionFeignClient;

    @Autowired
    VisitRecordFeignClient visitRecordFeignClient;

    @RequiresPermissions("aggregation:visitRecord:view")
    @RequestMapping("/visitRecordPage")
    public String visitRecordPage(HttpServletRequest request) {
        String ownOrgId = "";
        List<OrganizationDTO> businessGroupList = new ArrayList<>();
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        if(RoleCodeEnum.SWZJ.name().equals(roleCode)) {
            ownOrgId = String.valueOf(curLoginUser.getOrgId());
            //商务总监查他自己的组
            OrganizationDTO curOrgGroupByOrgId = getCurOrgGroupByOrgId(ownOrgId);
            if(curOrgGroupByOrgId!=null) {
                businessGroupList.add(curOrgGroupByOrgId);
            }
            request.setAttribute("businessGroupList", businessGroupList);
            request.setAttribute("ownOrgId", ownOrgId);
        }
        /*
         * UserInfoDTO curLoginUser = CommUtil.getCurLoginUser(); Long orgId =
         * curLoginUser.getOrgId(); // 签约项目 List<ProjectInfoDTO> projectList = getProjectList(); //
         * 商务小组 List<OrganizationDTO> businessGroupList = getBusinessGroupList(orgId,
         * OrgTypeConstant.SWZ); // 商务经理 List<UserInfoDTO> busManagerList = getUserInfo(orgId,
         * RoleCodeEnum.SWJL.name()); // 签约省份 SysRegionDTO sysRegionDTO = new SysRegionDTO();
         * sysRegionDTO.setType(0); JSONResult<List<SysRegionDTO>> proviceJr =
         * sysRegionFeignClient.querySysRegionByParam(sysRegionDTO); // 公司 // List<OrganizationDTO>
         * companyList = getBusinessGroupList(orgId, // OrgTypeConstant.ZSZX); OrganizationQueryDTO
         * companyDto = new OrganizationQueryDTO(); companyDto.setOrgType(OrgTypeConstant.SWZ);
         * JSONResult<List<OrganizationRespDTO>> companyJr =
         * organizationFeignClient.queryOrgByParam(companyDto);
         * 
         * request.setAttribute("projectList", projectList); request.setAttribute("busManagerList",
         * busManagerList); request.setAttribute("businessGroupList", businessGroupList);
         * request.setAttribute("proviceList", proviceJr.getData());
         * request.setAttribute("companyList", companyJr.getData());
         */

        return "visit/customerVisitRecord";
    }

    @RequiresPermissions("aggregation:visitRecord:view")
    @RequestMapping("/noVisitRecordPage")
    public String noVisitRecordPage(HttpServletRequest request) {
        String ownOrgId = "";
        List<OrganizationDTO> businessGroupList = new ArrayList<>();
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        Long orgId = curLoginUser.getOrgId();
//        List<ProjectInfoDTO> projectList = getProjectList();
//        List<OrganizationDTO> businessGroupList = getBusinessGroupList(orgId, OrgTypeConstant.SWZ);
        // 商务经理
        List<UserInfoDTO> busManagerList = getUserInfo(orgId, RoleCodeEnum.SWJL.name());
        // 签约省份
        JSONResult<List<SysRegionDTO>> proviceJr = sysRegionFeignClient.getproviceList();
        if(RoleCodeEnum.SWZJ.name().equals(roleCode)) {
            ownOrgId = String.valueOf(curLoginUser.getOrgId());
            //商务总监查他自己的组
            OrganizationDTO curOrgGroupByOrgId = getCurOrgGroupByOrgId(ownOrgId);
            if(curOrgGroupByOrgId!=null) {
                businessGroupList.add(curOrgGroupByOrgId);
            }
            request.setAttribute("businessGroupList", businessGroupList);
            request.setAttribute("ownOrgId", ownOrgId);
        }
        // 公司
        // List<OrganizationDTO> companyList = getBusinessGroupList(orgId,
        // OrgTypeConstant.ZSZX);
        OrganizationQueryDTO companyDto = new OrganizationQueryDTO();
        companyDto.setOrgType(OrgTypeConstant.SWZ);
        JSONResult<List<OrganizationRespDTO>> companyJr =
                organizationFeignClient.queryOrgByParam(companyDto);

        // 查询电销组
        OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
        orgDto.setOrgType(OrgTypeConstant.DXZ);
        JSONResult<List<OrganizationRespDTO>> orgJson =
                organizationFeignClient.queryOrgByParam(orgDto);
        if (orgJson.getCode().equals(JSONResult.SUCCESS)) {
            request.setAttribute("orgSelect", orgJson.getData());
        }

        // 查询所有签约项目
        ProjectInfoPageParam param=new ProjectInfoPageParam();
        param.setIsNotSign(1);
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.queryBySign(param);
        request.setAttribute("projectList", allProject.getData());
        // request.setAttribute("busManagerList",busManagerList);
        // request.setAttribute("businessGroupList",businessGroupList);
        request.setAttribute("proviceList", proviceJr.getData());
        // request.setAttribute("companyList", companyJr.getData());

        return "visit/customerNoVisitRecord";
    }

    /**
     * 获取所有的项目
     * 
     * @return
     */
    private List<ProjectInfoDTO> getProjectList() {
        JSONResult<List<ProjectInfoDTO>> projectInfoListJr = projectInfoFeignClient.allProject();
        if (projectInfoListJr == null || !JSONResult.SUCCESS.equals(projectInfoListJr.getCode())) {
            logger.error("signrecord ,get projectList ,res{{}}", projectInfoListJr);
            return null;
        }
        return projectInfoListJr.getData();
    }

    private List<UserInfoDTO> getUserInfo(Long orgId, String roleName) {
        UserOrgRoleReq req = new UserOrgRoleReq();
        if (orgId != null) {
            req.setOrgId(orgId);
        }
        req.setRoleCode(roleName);
        JSONResult<List<UserInfoDTO>> userJr = userInfoFeignClient.listByOrgAndRole(req);
        if (userJr == null || !JSONResult.SUCCESS.equals(userJr.getCode())) {
            logger.error(
                    "查询电销通话记录-获取电销顾问-userInfoFeignClient.listByOrgAndRole(req),param{{}},res{{}}",
                    req, userJr);
            return null;
        }
        return userJr.getData();
    }

    /**
     * 获取商务组
     * 
     * @param orgId
     * @param orgType
     * @return
     */
    private List<OrganizationDTO> getBusinessGroupList(Long orgId, Integer orgType) {
        OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
        busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
        busGroupReqDTO.setParentId(orgId);
        busGroupReqDTO.setOrgType(orgType);
        JSONResult<List<OrganizationDTO>> orgJr =
                organizationFeignClient.listDescenDantByParentId(busGroupReqDTO);
        if (orgJr == null || !JSONResult.SUCCESS.equals(orgJr.getCode())) {
            logger.error("query org list res{{}}", orgJr);
            return null;
        }
        return orgJr.getData();

    }

    /**
     * 获取所有的 组
     * 
     * @param orgType
     * @return
     */
    private List<OrganizationRespDTO> getTeleGroupList(Integer orgType) {
        OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
        busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
        busGroupReqDTO.setOrgType(orgType);
        JSONResult<List<OrganizationRespDTO>> orgJr =
                organizationFeignClient.queryOrgByParam(busGroupReqDTO);
        if (orgJr == null || !JSONResult.SUCCESS.equals(orgJr.getCode())) {
            logger.error("query org list res{{}}", orgJr);
            return null;
        }
        return orgJr.getData();
    }

    /**
     * 查询 客户到访记录
     * 
     * @param visitRecordReqDTO
     * @return
     */
    @RequiresPermissions("aggregation:visitRecord:view")
    @PostMapping("/listVisitRecord")
    @ResponseBody
    public JSONResult<PageBean<VisitRecordRespDTO>> listVisitRecord(
            @RequestBody VisitRecordReqDTO visitRecordReqDTO) {
        handleReqParam(visitRecordReqDTO);

        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long orgId = curLoginUser.getOrgId();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        Long busGroupId = visitRecordReqDTO.getBusGroupId();
        List<Long> busGroupIdList = new ArrayList<>();
        if (RoleCodeEnum.SWDQZJ.name().equals(roleCode)) {
            /*Long busManagerId = visitRecordReqDTO.getBusManagerId();
            if (busManagerId == null) {
                List<Long> accountIdList = getAccountIdList(orgId, RoleCodeEnum.SWJL.name());
                if (CollectionUtils.isEmpty(accountIdList)) {
                    return new JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(),
                            "该用户下没有下属");
                }
                visitRecordReqDTO.setBusManagerIdList(accountIdList);
            } else {
                List<Long> busManagerIdList = new ArrayList<>();
                busManagerIdList.add(busManagerId);
                visitRecordReqDTO.setBusManagerIdList(busManagerIdList);
            }*/

            //商务经理外调，发起外调的商务总监进行审核,根据组id查询
            if (busGroupId == null) {
                // 查询下级所有商务组
                OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
                queryDTO.setParentId(orgId);
                queryDTO.setOrgType(OrgTypeConstant.SWZ);
                JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                        organizationFeignClient.queryOrgByParam(queryDTO);
                List<OrganizationRespDTO> data = queryOrgByParam.getData();
                if (CollectionUtils.isEmpty(data)) {
                    return new JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(),
                            "该用户下没有下属");
                }
                busGroupIdList = data.stream().map(OrganizationRespDTO::getId).collect(Collectors.toList());
                visitRecordReqDTO.setBusGroupIdList(busGroupIdList);
            } else {
                busGroupIdList.add(busGroupId);
                visitRecordReqDTO.setBusGroupIdList(busGroupIdList);
            }

        } else if (RoleCodeEnum.SWZJ.name().equals(roleCode)) {
            //商务经理外调，发起外调的商务总监进行审核,根据组id查询
            List<Long> accountIdList = getAccountIdList(orgId, RoleCodeEnum.SWJL.name());
            if (CollectionUtils.isEmpty(accountIdList)) {
                return new JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(),
                        "该用户下没有下属");
            }
            busGroupIdList.add(orgId);
            visitRecordReqDTO.setBusGroupIdList(busGroupIdList);
        } else {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(), "角色没有权限");
        }

        return visitRecordFeignClient.listVisitRecord(visitRecordReqDTO);
    }

    /**
     * 查询 客户到访记录
     * 
     * @param visitNoRecordReqDTO
     * @return
     */
    @RequiresPermissions("aggregation:visitRecord:view")
    @PostMapping("/listNoVisitRecord")
    @ResponseBody
    public JSONResult<PageBean<VisitNoRecordRespDTO>> listNoVisitRecord(
            @RequestBody VisitNoRecordReqDTO visitNoRecordReqDTO) {
        // 获取当前账号角色 机构信息
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long orgId = curLoginUser.getOrgId();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        List<Long> busGroupIdList = new ArrayList<Long>();

        if (RoleCodeEnum.SWDQZJ.name().equals(roleCode)) {
            if(null != visitNoRecordReqDTO.getBusGroupId()){
                busGroupIdList.add(visitNoRecordReqDTO.getBusGroupId());
            } else {
                // 查询下级所有商务组
                OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
                queryDTO.setParentId(orgId);
                queryDTO.setOrgType(OrgTypeConstant.SWZ);
                JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                    organizationFeignClient.queryOrgByParam(queryDTO);
                List<OrganizationRespDTO> data = queryOrgByParam.getData();
                if (CollectionUtils.isEmpty(data)) {
                    return new JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(),
                        "该用户下没有下属");
                }
                for (OrganizationRespDTO organizationRespDTO : data) {
                    busGroupIdList.add(organizationRespDTO.getId());
                }
            }
        } else if (RoleCodeEnum.SWZJ.name().equals(roleCode)) {
            busGroupIdList.add(orgId);
        } else if (RoleCodeEnum.GLY.name().equals(roleCode)) {
        } else {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(), "角色没有权限");
        }
        visitNoRecordReqDTO.setBusGroupIdList(busGroupIdList);
        //visitNoRecordReqDTO.setStatus(1);
        logger.info("listVisitRecord,curLoginUser{{}},reqParam{{}}", curLoginUser,
                visitNoRecordReqDTO);

//        if (null == visitNoRecordReqDTO.getStatus()) {
//            visitNoRecordReqDTO.setStatus(Constants.IS_LOGIN_UP);
//
//        }

        JSONResult<PageBean<VisitNoRecordRespDTO>> visitList =
                visitRecordFeignClient.listNoVisitRecord(visitNoRecordReqDTO);
        return visitList;
    }

    /**
     * 获取当前组织机构下 角色信息
     * 
     * @param orgId
     * @param roleCode
     * @return
     */
    private List<Long> getAccountIdList(Long orgId, String roleCode) {
        UserOrgRoleReq req = new UserOrgRoleReq();
        req.setOrgId(orgId);
        req.setRoleCode(roleCode);
        JSONResult<List<UserInfoDTO>> userJr = userInfoFeignClient.listByOrgAndRole(req);
        if (userJr == null || !JSONResult.SUCCESS.equals(userJr.getCode())) {
            logger.error(
                    "查询电销通话记录-获取电销顾问-userInfoFeignClient.listByOrgAndRole(req),param{{}},res{{}}",
                    req, userJr);
            return null;
        }
        List<UserInfoDTO> userInfoDTOList = userJr.getData();
        if (userInfoDTOList != null && userInfoDTOList.size() != 0) {
            List<Long> idList =
                    userInfoDTOList.stream().map(UserInfoDTO::getId).collect(Collectors.toList());
            return idList;
        }
        return null;
    }

    private void handleReqParam(VisitRecordReqDTO visitRecordReqDTO) {
        Long projectId = visitRecordReqDTO.getProjectId();
        if (projectId != null) {
            List<Long> projectIdList = new ArrayList<Long>();
            projectIdList.add(projectId);
            visitRecordReqDTO.setProjectIdList(projectIdList);
        }
        Long busGroupId = visitRecordReqDTO.getBusGroupId();
        if (busGroupId != null) {
            List<Long> busGroupIdList = new ArrayList<>();
            busGroupIdList.add(busGroupId);
            visitRecordReqDTO.setBusGroupIdList(busGroupIdList);
        }
        Long companyId = visitRecordReqDTO.getCompanyId();
        if (companyId != null) {
            List<Long> companyIdList = new ArrayList<>();
            companyIdList.add(companyId);
            visitRecordReqDTO.setCompanyIdList(companyIdList);
        }

    }

    /**
     * 驳回签约单
     * 
     * @return
     */
    @RequiresPermissions("aggregation:visitRecord:reject")
    @PostMapping("/rejectVisitRecord")
    @LogRecord(description = "来访记录驳回", operationType = LogRecord.OperationType.UPDATE,
            menuName = MenuEnum.CUSTOMER_VISIT_RECORD)
    @ResponseBody
    public JSONResult<Boolean> rejectVisitRecord(@Valid @RequestBody RejectVisitRecordReqDTO reqDTO,
            BindingResult result) {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO userInfo = CommUtil.getCurLoginUser();
        reqDTO.setAuditPerson(userInfo.getId());
        reqDTO.setStatus(AggregationConstant.VISIT_RECORD_STATUS.REJECT);
        return visitRecordFeignClient.rejectVisitRecord(reqDTO);
    }

    /**
     * 审核通过 签约单
     * 
     * @return
     */
    @RequiresPermissions("aggregation:visitRecord:pass")
    @PostMapping("/passAuditSignOrder")
    @LogRecord(description = "来访记录审核通过", operationType = LogRecord.OperationType.UPDATE,
            menuName = MenuEnum.CUSTOMER_VISIT_RECORD)
    @ResponseBody
    public JSONResult<Boolean> passAuditSignOrder(
            @Valid @RequestBody RejectVisitRecordReqDTO reqDTO, BindingResult result) {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        reqDTO.setStatus(AggregationConstant.VISIT_RECORD_STATUS.PASS);

        return visitRecordFeignClient.rejectVisitRecord(reqDTO);
    }


    /***
     * 签约省份
     *
     * @return
     */
    @ResponseBody
    @PostMapping("/getSignProvince")
    public JSONResult<List<SysRegionDTO>> getSignProvince() {
        SysRegionDTO sysRegionDTO = new SysRegionDTO();
        sysRegionDTO.setType(0);
        return sysRegionFeignClient.querySysRegionByParam(sysRegionDTO);
    }

    /**
     * 获取当前 orgId所在的组织
     * @param orgId
     * @param
     * @return
     */
    private OrganizationDTO getCurOrgGroupByOrgId(String orgId) {
        // 电销组
        IdEntity idEntity = new IdEntity();
        idEntity.setId(orgId+"");
        JSONResult<OrganizationDTO> orgJr = organizationFeignClient.queryOrgById(idEntity);
        if(!JSONResult.SUCCESS.equals(orgJr.getCode())) {
            logger.error("getCurOrgGroupByOrgId,param{{}},res{{}}",idEntity,orgJr);
            return null;
        }
        return orgJr.getData();
    }

}
