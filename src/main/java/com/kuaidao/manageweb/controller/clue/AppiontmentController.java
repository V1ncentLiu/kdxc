
package com.kuaidao.manageweb.controller.clue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import com.kuaidao.businessconfig.constant.AggregationConstant;
import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.businessconfig.dto.project.ProjectInfoPageParam;
import org.apache.shiro.SecurityUtils;
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
import com.kuaidao.aggregation.dto.clue.AppiontmentCancelDTO;
import com.kuaidao.aggregation.dto.clue.ClueAppiontmentDTO;
import com.kuaidao.aggregation.dto.clue.ClueAppiontmentPageParam;
import com.kuaidao.aggregation.dto.clue.ClueAppiontmentReq;
import com.kuaidao.aggregation.dto.clue.ClueRepeatPhoneDTO;
import com.kuaidao.common.constant.CluePhase;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.clue.AppiontmentFeignClient;
import com.kuaidao.manageweb.feign.customfield.CustomFieldFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.dto.customfield.CustomFieldQueryDTO;
import com.kuaidao.sys.dto.customfield.QueryFieldByRoleAndMenuReq;
import com.kuaidao.sys.dto.customfield.QueryFieldByUserAndMenuReq;
import com.kuaidao.sys.dto.customfield.UserFieldDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

/**
 * @author zxy
 *
 */

@Controller
@RequestMapping("/clue/appiontment")
public class AppiontmentController {
    private static Logger logger = LoggerFactory.getLogger(AppiontmentController.class);
    @Autowired
    private AppiontmentFeignClient appiontmentFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;
    @Autowired
    private CustomFieldFeignClient customFieldFeignClient;
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;

    /***
     * 预约来访列表页
     * 
     * @return
     */
    @RequestMapping("/initAppiontmentList")
    @RequiresPermissions("aggregation:appiontmentManager:view")
    public String initAppiontmentList(HttpServletRequest request) {
        UserInfoDTO user = getUser();
        List<RoleInfoDTO> roleList = user.getRoleList();
        String ownOrgId = "";
        // 如果当前登录的为电销总监,查询所有下属电销员工
        if (roleList != null && RoleCodeEnum.DXZJ.name().equals(roleList.get(0).getRoleCode())) {
            List<OrganizationDTO> orgList = new ArrayList<OrganizationDTO>();
            UserOrgRoleReq userOrgRoleReq = new UserOrgRoleReq();
            userOrgRoleReq.setOrgId(user.getOrgId());
            userOrgRoleReq.setRoleCode(RoleCodeEnum.DXCYGW.name());
            JSONResult<List<UserInfoDTO>> listByOrgAndRole =
                    userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
            request.setAttribute("userList", listByOrgAndRole.getData());
            ownOrgId =  String.valueOf(user.getOrgId());
            OrganizationDTO curOrgGroupByOrgId = getCurOrgGroupByOrgId(ownOrgId);
            if(curOrgGroupByOrgId!=null) {
                orgList.add(curOrgGroupByOrgId);
            }
            request.setAttribute("orgList", orgList);
            request.setAttribute("ownOrgId", ownOrgId);
        } else if (roleList != null
                && RoleCodeEnum.DXFZ.name().equals(roleList.get(0).getRoleCode())) {
            // 如果当前登录的为电销副总,查询所有下属电销组
            OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
            organizationQueryDTO.setParentId(user.getOrgId());
            organizationQueryDTO.setOrgType(OrgTypeConstant.DXZ);
            JSONResult<List<OrganizationDTO>> listDescenDantByParentId =
                    organizationFeignClient.listDescenDantByParentId(organizationQueryDTO);
            request.setAttribute("orgList", listDescenDantByParentId.getData());
        } else if (roleList != null
                && RoleCodeEnum.DXZJL.name().equals(roleList.get(0).getRoleCode())) {
            // 如果当前登录的为电销总经理,查询所有下属电销组
            OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
            organizationQueryDTO.setParentId(user.getOrgId());
            organizationQueryDTO.setOrgType(OrgTypeConstant.DXZ);
            JSONResult<List<OrganizationDTO>> listDescenDantByParentId =
                    organizationFeignClient.listDescenDantByParentId(organizationQueryDTO);
            request.setAttribute("orgList", listDescenDantByParentId.getData());
            // 如果当前登录的为电销总经理,查询所有下属事业部
            organizationQueryDTO.setOrgType(OrgTypeConstant.DZSYB);
            JSONResult<List<OrganizationDTO>> deptList =
                    organizationFeignClient.listDescenDantByParentId(organizationQueryDTO);
            request.setAttribute("deptList", deptList.getData());
        } else if (roleList != null
                && RoleCodeEnum.GLY.name().equals(roleList.get(0).getRoleCode())) {
            // 管理员查询所有电销组
            OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
            queryDTO.setOrgType(OrgTypeConstant.DXZ);
            JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                    organizationFeignClient.queryOrgByParam(queryDTO);
            request.setAttribute("orgList", queryOrgByParam.getData());
        }
        // 查询字典类别集合
        request.setAttribute("clueCategoryList", getDictionaryByCode(Constants.CLUE_CATEGORY));
        // 查询所有签约项目
        ProjectInfoPageParam param=new ProjectInfoPageParam();
        param.setIsNotSign(AggregationConstant.NO);
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.queryBySign(param);
        request.setAttribute("projectList", allProject.getData());

        // 根据角色查询页面字段
        QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq = new QueryFieldByRoleAndMenuReq();
        queryFieldByRoleAndMenuReq.setMenuCode("aggregation:appiontmentManager");
        queryFieldByRoleAndMenuReq.setId(user.getRoleList().get(0).getId());
        JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu =
                customFieldFeignClient.queryFieldByRoleAndMenu(queryFieldByRoleAndMenuReq);
        request.setAttribute("fieldList", queryFieldByRoleAndMenu.getData());

        // 根据用户查询页面字段
        QueryFieldByUserAndMenuReq queryFieldByUserAndMenuReq = new QueryFieldByUserAndMenuReq();
        queryFieldByUserAndMenuReq.setId(user.getId());
        queryFieldByUserAndMenuReq.setRoleId(user.getRoleList().get(0).getId());
        queryFieldByUserAndMenuReq.setMenuCode("aggregation:appiontmentManager");
        JSONResult<List<UserFieldDTO>> queryFieldByUserAndMenu =
                customFieldFeignClient.queryFieldByUserAndMenu(queryFieldByUserAndMenuReq);
        request.setAttribute("userFieldList", queryFieldByUserAndMenu.getData());
        request.setAttribute("userId", user.getId().toString());
        request.setAttribute("roleCode", roleList.get(0).getRoleCode());
        request.setAttribute("orgId", user.getOrgId().toString());
        return "clue/appiontmentManagerPage";
    }

    /***
     * 预约来访列表
     * 
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    @RequiresPermissions("aggregation:appiontmentManager:view")
    public JSONResult<PageBean<ClueAppiontmentDTO>> list(
            @RequestBody ClueAppiontmentPageParam pageParam, HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        pageParam.setUserId(user.getId());
        pageParam.setOrgId(user.getOrgId());
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {

            pageParam.setRoleCode(roleList.get(0).getRoleCode());
        }
        JSONResult<PageBean<ClueAppiontmentDTO>> list = appiontmentFeignClient.list(pageParam);

        return list;
    }

    /***
     * 重复手机号资源信息
     * 
     * @return
     */
    @PostMapping("/repeatPhonelist")
    @ResponseBody
    @RequiresPermissions("aggregation:appiontmentManager:view")
    public JSONResult<List<ClueRepeatPhoneDTO>> repeatPhonelist(
            @RequestBody ClueAppiontmentReq param, HttpServletRequest request) {
        JSONResult<List<ClueRepeatPhoneDTO>> list = appiontmentFeignClient.repeatPhonelist(param);
        return list;
    }

    /***
     * 重复手机号资源信息
     * 
     * @return
     */
    @PostMapping("/repeatPhoneMap")
    @ResponseBody
    public JSONResult<Map> repeatPhoneMap(@RequestBody ClueAppiontmentReq param,
            HttpServletRequest request) {
    	 UserInfoDTO user = getUser();
    	 if(user.getBusinessLine() !=null ) {
    		 param.setBusinessLine(user.getBusinessLine());
    		 List<Integer> phaseList = new ArrayList<>();
    		 if(user.getRoleList() !=null && user.getRoleList().size()>0) {
    			 RoleInfoDTO roleInfoDTO = user.getRoleList().get(0);
    			 if(RoleCodeEnum.HWY.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.HWJL.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.HWZG.name().equals(roleInfoDTO.getRoleCode())) {
    				 phaseList.add(CluePhase.PHASE_1ST.getCode());
    				 phaseList.add(CluePhase.PHAE_2ND.getCode());
    				 phaseList.add(CluePhase.PHAE_3RD.getCode());
    				 phaseList.add(CluePhase.PHAE_4TH.getCode());
    				 phaseList.add(CluePhase.PHAE_6TH.getCode());
    				 phaseList.add(CluePhase.PHAE_10TH.getCode());
    				 phaseList.add(CluePhase.PHAE_11TH.getCode());
    				 phaseList.add(CluePhase.PHAE_12TH.getCode());
    			 }else {
    				 phaseList.add(CluePhase.PHASE_1ST.getCode());
    				 phaseList.add(CluePhase.PHAE_2ND.getCode());
    			 }
    		 }
    		 param.setPhaseList(phaseList);
    	 }
    	
        JSONResult<Map> map = appiontmentFeignClient.repeatPhoneMap(param);
        return map;
    }

    /***
     * 查询预约来访
     * 
     * @return
     */
    @RequestMapping("/getAppiontment")
    @ResponseBody
    @RequiresPermissions("aggregation:appiontmentManager:view")
    public JSONResult<ClueAppiontmentDTO> getAppiontment(@RequestBody IdEntityLong id,
            HttpServletRequest request) {
        // 查询公司信息
        return appiontmentFeignClient.get(id);
    }

    /***
     * 取消预约来访数据
     * 
     * @return
     */
    @RequestMapping("/cancelAppiontment")
    @ResponseBody
    @RequiresPermissions("aggregation:appiontmentManager:cancel")
    @LogRecord(description = "取消预约来访数据", operationType = OperationType.UPDATE,
            menuName = MenuEnum.APPIONTMENT_MANAGEMENT)
    public JSONResult<String> cancelAppiontment(@RequestBody AppiontmentCancelDTO dto,
            HttpServletRequest request) {

        // 取消邀约来访数据
        return appiontmentFeignClient.cancelAppiontment(dto);
    }

    /***
     * 查询取消预约来访数据
     * 
     * @return
     */
    @RequestMapping("/findCancelList")
    @ResponseBody
    public JSONResult<List<ClueAppiontmentDTO>> findCancelList(
            @RequestBody AppiontmentCancelDTO dto, HttpServletRequest request) {

        // 查询邀约数据是否可以取消
        return appiontmentFeignClient.findCancelList(dto);
    }

    /**
     * 修改预约来访信息
     * 
     * @param clueAppiontmentReq
     * @return
     */
    @PostMapping("/updateAppiontment")
    @ResponseBody
    @RequiresPermissions("aggregation:appiontmentManager:edit")
    @LogRecord(description = "修改预约来访信息", operationType = OperationType.UPDATE,
            menuName = MenuEnum.APPIONTMENT_MANAGEMENT)
    public JSONResult updateAppiontment(@Valid @RequestBody ClueAppiontmentReq clueAppiontmentReq,
            BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }

        Long id = clueAppiontmentReq.getId();
        if (id == null) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        return appiontmentFeignClient.update(clueAppiontmentReq);
    }

    /**
     * 删除预约来访信息
     * 
     * @param idList
     * @return
     */
    @PostMapping("/deleteAppiontment")
    @ResponseBody
    @LogRecord(description = "删除预约来访信息", operationType = OperationType.DELETE,
            menuName = MenuEnum.APPIONTMENT_MANAGEMENT)
    public JSONResult deleteAppiontment(@RequestBody IdListLongReq idList) {
        UserInfoDTO user = getUser();
        Long loginUserId = user.getId();
        return appiontmentFeignClient.delete(idList, loginUserId);
    }

    /***
     * 下属电销员工列表
     * 
     * @return
     */
    @PostMapping("/getSaleList")
    @ResponseBody
    @RequiresPermissions("aggregation:appiontmentManager:view")
    public JSONResult<List<UserInfoDTO>> getSaleList(@RequestBody UserOrgRoleReq userOrgRoleReq,
            HttpServletRequest request) {
        userOrgRoleReq.setRoleCode(RoleCodeEnum.DXCYGW.name());
        JSONResult<List<UserInfoDTO>> listByOrgAndRole =
                userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        return listByOrgAndRole;
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
