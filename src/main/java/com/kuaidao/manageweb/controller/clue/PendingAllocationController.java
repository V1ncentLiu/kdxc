/**
 * 
 */
package com.kuaidao.manageweb.controller.clue;

import com.kuaidao.manageweb.feign.user.SysSettingFeignClient;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.kuaidao.sys.dto.user.SysSettingDTO;
import com.kuaidao.sys.dto.user.SysSettingReq;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.kuaidao.aggregation.dto.clue.AllocationClueReq;
import com.kuaidao.aggregation.dto.clue.PendingAllocationClueDTO;
import com.kuaidao.aggregation.dto.clue.PendingAllocationCluePageParam;
import com.kuaidao.businessconfig.dto.assignrule.InfoAssignDTO;
import com.kuaidao.businessconfig.dto.assignrule.InfoAssignQueryDTO;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.assignrule.InfoAssignFeignClient;
import com.kuaidao.manageweb.feign.clue.ClueBasicFeignClient;
import com.kuaidao.manageweb.feign.customfield.CustomFieldFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.customfield.CustomFieldQueryDTO;
import com.kuaidao.sys.dto.customfield.QueryFieldByRoleAndMenuReq;
import com.kuaidao.sys.dto.customfield.QueryFieldByUserAndMenuReq;
import com.kuaidao.sys.dto.customfield.UserFieldDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

/**
 * @author zxy
 *
 */

@Controller
@RequestMapping("/clue/pendingAllocation")
public class PendingAllocationController {
    private static Logger logger = LoggerFactory.getLogger(PendingAllocationController.class);
    @Autowired
    private ClueBasicFeignClient clueBasicFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;
    @Autowired
    private InfoAssignFeignClient infoAssignFeignClient;
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;
    @Autowired
    private CustomFieldFeignClient customFieldFeignClient;
    @Autowired
    private SysSettingFeignClient sysSettingFeignClient;
    /***
     * 待分配资源列表页
     * 
     * @return
     */
    @RequestMapping("/initAppiontmentList")
    @RequiresPermissions("aggregation:pendingAllocationManager:view")
    public String initCompanyList(HttpServletRequest request,  @RequestParam(required = false) Integer type) {
        UserInfoDTO user = getUser();
        List<RoleInfoDTO> roleList = user.getRoleList();
        String ownOrgId = "";
        if (roleList != null && RoleCodeEnum.DXZJ.name().equals(roleList.get(0).getRoleCode())) {
            List<OrganizationRespDTO>  teleGorupList = new ArrayList<>();
            // 如果当前登录的为电销总监,查询所有下属电销员工
            List<Integer> statusList = new ArrayList<Integer>();
            statusList.add(SysConstant.USER_STATUS_ENABLE);
            statusList.add(SysConstant.USER_STATUS_LOCK);
            List<UserInfoDTO> userList =
                    getUserList(user.getOrgId(), RoleCodeEnum.DXCYGW.name(), statusList);
            request.setAttribute("saleList", userList);
            ownOrgId =  String.valueOf(user.getOrgId());
            OrganizationDTO curOrgGroupByOrgId = getCurOrgGroupByOrgId(ownOrgId);
            if(curOrgGroupByOrgId!=null) {
                OrganizationRespDTO organizationRespDTO = new OrganizationRespDTO();
                organizationRespDTO.setId(curOrgGroupByOrgId.getId());
                organizationRespDTO.setName(curOrgGroupByOrgId.getName());
                teleGorupList.add(organizationRespDTO);
            }
            // 查询同事业部下的电销组
            Long orgId = user.getOrgId();
            JSONResult<OrganizationDTO> queryOrgById =
                    organizationFeignClient.queryOrgById(new IdEntity(orgId.toString()));
            List<Map<String, Object>> saleGroupList =
                    getSaleGroupList(queryOrgById.getData().getParentId(), user);
            request.setAttribute("teleGorupList", teleGorupList);
            request.setAttribute("ownOrgId", ownOrgId);
            request.setAttribute("saleGroupList", saleGroupList);


        } else if (roleList != null
                && RoleCodeEnum.DXFZ.name().equals(roleList.get(0).getRoleCode())) {
            // 如果当前登录的为电销副总,查询所有下属电销组
            List<Map<String, Object>> saleGroupList = getSaleGroupList(user.getOrgId(), user);
            request.setAttribute("orgList", saleGroupList);
            request.setAttribute("saleGroupList", saleGroupList);
            // 电销组查询筛选条件
            OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
            organizationQueryDTO.setParentId(user.getOrgId());
            organizationQueryDTO.setOrgType(OrgTypeConstant.DXZ);
            organizationQueryDTO.setBusinessLine(user.getBusinessLine());
            // 查询下级电销组
            JSONResult<List<OrganizationDTO>> listDescenDantByParentId =
                    organizationFeignClient.listDescenDantByParentId(organizationQueryDTO);
            request.setAttribute("teleGorupList", listDescenDantByParentId.getData());
        } else if (roleList != null
                && RoleCodeEnum.DXZJL.name().equals(roleList.get(0).getRoleCode())) {
            // 如果当前登录的为电销总经理,查询所有下属电销组
            List<Map<String, Object>> saleGroupList = getSaleGroupList(user.getOrgId(), user);
            request.setAttribute("orgList", saleGroupList);
            request.setAttribute("saleGroupList", saleGroupList);
            // 电销组查询筛选条件
            OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
            organizationQueryDTO.setParentId(user.getOrgId());
            organizationQueryDTO.setOrgType(OrgTypeConstant.DXZ);
            organizationQueryDTO.setBusinessLine(user.getBusinessLine());
            // 查询下级电销组
            JSONResult<List<OrganizationDTO>> listDescenDantByParentId =
                    organizationFeignClient.listDescenDantByParentId(organizationQueryDTO);
            request.setAttribute("teleGorupList", listDescenDantByParentId.getData());
        }
        // 查询所有信息流分配规则
        InfoAssignQueryDTO infoAssignQueryDTO = new InfoAssignQueryDTO();
        infoAssignQueryDTO.setPageNum(1);
        infoAssignQueryDTO.setPageSize(1000);
        JSONResult<PageBean<InfoAssignDTO>> pageBean =
                infoAssignFeignClient.queryInfoAssignPage(infoAssignQueryDTO);
        if (pageBean.getData() != null) {
            request.setAttribute("ruleList", pageBean.getData().getData());
        }

        // 查询字典类别集合
        request.setAttribute("clueCategoryList", getDictionaryByCode(Constants.CLUE_CATEGORY));
        // 查询字典类别集合
        request.setAttribute("clueTypeList", getDictionaryByCode(Constants.CLUE_TYPE));
        // 查询字典释放原因集合
        request.setAttribute("releaseReasonList", getDictionaryByCode(Constants.RELEASE_REASON));
        // 查询字典电销一级客户状态集合
        request.setAttribute("customerStatusList",
                getDictionaryByCode(DicCodeEnum.CUSTOMERSTATUS.getCode()));
        // 查询字典电销二级客户状态集合
        request.setAttribute("customerStatusSubList",
                getDictionaryByCode(DicCodeEnum.CUSTOMERSTATUSSUB.getCode()));
        // 查询字典话务一级客户状态集合
        request.setAttribute("phCustomerStatusList",
                getDictionaryByCode(DicCodeEnum.PHCUSTOMERSTATUS.getCode()));
        // 查询字典话务二级客户状态集合
        request.setAttribute("phCustomerStatusSubList",
                getDictionaryByCode(DicCodeEnum.PHCUSTOMERSTATUSSUB.getCode()));
        // 查询字典客户级别集合
        request.setAttribute("levelList", getDictionaryByCode(DicCodeEnum.CUSLEVEL.getCode()));

        // 根据角色查询页面字段
        QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq = new QueryFieldByRoleAndMenuReq();
        queryFieldByRoleAndMenuReq.setMenuCode("aggregation:pendingAllocationManager");
        queryFieldByRoleAndMenuReq.setId(user.getRoleList().get(0).getId());
        JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu =
                customFieldFeignClient.queryFieldByRoleAndMenu(queryFieldByRoleAndMenuReq);
        request.setAttribute("fieldList", queryFieldByRoleAndMenu.getData());
        // 根据用户查询页面字段
        QueryFieldByUserAndMenuReq queryFieldByUserAndMenuReq = new QueryFieldByUserAndMenuReq();
        queryFieldByUserAndMenuReq.setId(user.getId());
        queryFieldByUserAndMenuReq.setRoleId(user.getRoleList().get(0).getId());
        queryFieldByUserAndMenuReq.setMenuCode("aggregation:pendingAllocationManager");
        JSONResult<List<UserFieldDTO>> queryFieldByUserAndMenu =
                customFieldFeignClient.queryFieldByUserAndMenu(queryFieldByUserAndMenuReq);
        request.setAttribute("userFieldList", queryFieldByUserAndMenu.getData());
        request.setAttribute("type", type);
        request.setAttribute("businessLine", user.getBusinessLine());
        Boolean canCopy = handleCanCopy(user.getBusinessLine());
        request.setAttribute("canCopy", canCopy);
        return "clue/pendingAllocationManagerPage";
    }

    /***
     * 待分配资源列表
     * 
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    @RequiresPermissions("aggregation:pendingAllocationManager:view")
    public JSONResult<PageBean<PendingAllocationClueDTO>> list(
            @RequestBody PendingAllocationCluePageParam pageParam, HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        pageParam.setUserId(user.getId());
        pageParam.setOrgId(user.getOrgId());
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {
            pageParam.setRoleCode(roleList.get(0).getRoleCode());
        }
        JSONResult<PageBean<PendingAllocationClueDTO>> pendingAllocationList =
                clueBasicFeignClient.pendingAllocationList(pageParam);
        return pendingAllocationList;
    }


    /**
     * 分配资源
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/allocationClue")
    @ResponseBody
    @RequiresPermissions("aggregation:pendingAllocationManager:allocation")
    @LogRecord(description = "分配资源", operationType = OperationType.DISTRIBUTION,
            menuName = MenuEnum.TELE_CUSTOMER_MANAGER)
    public JSONResult allocationClue(@Valid @RequestBody AllocationClueReq allocationClueReq,
            BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        allocationClueReq.setUserId(user.getId());
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {
            allocationClueReq.setRoleId(roleList.get(0).getId());
            allocationClueReq.setRoleCode(roleList.get(0).getRoleCode());
        }
        allocationClueReq.setOrg(user.getOrgId());
        allocationClueReq.setBusinessLine(user.getBusinessLine());
        return clueBasicFeignClient.allocationClue(allocationClueReq);
    }

    /**
     * 转移资源
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/transferClue")
    @ResponseBody
    @RequiresPermissions("aggregation:pendingAllocationManager:transfer")
    @LogRecord(description = "转移资源", operationType = OperationType.TRANSFER,
            menuName = MenuEnum.PENDING_ALLOCATION_CLUE)
    public JSONResult transferClue(@Valid @RequestBody AllocationClueReq allocationClueReq,
            BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        allocationClueReq.setUserId(user.getId());
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {
            allocationClueReq.setRoleId(roleList.get(0).getId());
            allocationClueReq.setRoleCode(roleList.get(0).getRoleCode());
        }
        allocationClueReq.setOrg(user.getOrgId());
        return clueBasicFeignClient.transferClue(allocationClueReq);
    }

    /**
     * 批量分发
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/batchDistributionClue")
    @ResponseBody
    @RequiresPermissions("aggregation:pendingAllocationManager:batch")
    @LogRecord(description = "批量分发", operationType = OperationType.DISTRIBUTION,
            menuName = MenuEnum.PENDING_ALLOCATION_CLUE)
    public JSONResult batchDistributionClue(@Valid @RequestBody AllocationClueReq allocationClueReq,
            BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        allocationClueReq.setUserId(user.getId());
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {

            allocationClueReq.setRoleId(roleList.get(0).getId());
            allocationClueReq.setRoleCode(roleList.get(0).getRoleCode());
        }
        allocationClueReq.setOrg(user.getOrgId());
        return clueBasicFeignClient.batchDistributionClue(allocationClueReq);
    }

    /**
     * 副总分发
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/ceoDistributionClue")
    @ResponseBody
    @RequiresPermissions("aggregation:pendingAllocationManager:ceo")
    @LogRecord(description = "副总分发", operationType = OperationType.DISTRIBUTION,
            menuName = MenuEnum.PENDING_ALLOCATION_CLUE)
    public JSONResult ceoDistributionClue(@Valid @RequestBody AllocationClueReq allocationClueReq,
            BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        allocationClueReq.setUserId(user.getId());
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {
            allocationClueReq.setRoleId(roleList.get(0).getId());
            allocationClueReq.setRoleCode(roleList.get(0).getRoleCode());
        }
        allocationClueReq.setOrg(user.getOrgId());
        return clueBasicFeignClient.ceoDistributionClue(allocationClueReq);
    }



    /***
     * 下属电销员工列表
     * 
     * @return
     */
    @PostMapping("/getSaleList")
    @ResponseBody
    public JSONResult<List<UserInfoDTO>> getSaleList(@RequestBody UserOrgRoleReq userOrgRoleReq,
            HttpServletRequest request) {
        List<Integer> statusList = new ArrayList<Integer>();
        statusList.add(SysConstant.USER_STATUS_ENABLE);
        statusList.add(SysConstant.USER_STATUS_LOCK);
        userOrgRoleReq.setStatusList(statusList);
        userOrgRoleReq.setRoleCode(RoleCodeEnum.DXCYGW.name());
        JSONResult<List<UserInfoDTO>> listByOrgAndRole =
                userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        return listByOrgAndRole;
    }

    /**
     * 获取当前登录账号
     * 
     * @param orgDTO
     * @return
     */
    private UserInfoDTO getUser() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user;
    }

    /**
     * 获取电销组
     * 
     * @param orgDTO
     * @return
     */
    private List<Map<String, Object>> getSaleGroupList(Long orgId, UserInfoDTO userInfo) {
        OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
        organizationQueryDTO.setParentId(orgId);
        organizationQueryDTO.setOrgType(OrgTypeConstant.DXZ);
        if (userInfo.getBusinessLine() != null) {
            organizationQueryDTO.setBusinessLine(userInfo.getBusinessLine());
        }
        // 查询下级电销组
        JSONResult<List<OrganizationDTO>> listDescenDantByParentId =
                organizationFeignClient.listDescenDantByParentId(organizationQueryDTO);
        List<OrganizationDTO> data = listDescenDantByParentId.getData();
        // 查询所有电销总监
        List<Integer> statusList = new ArrayList<Integer>();
        statusList.add(SysConstant.USER_STATUS_ENABLE);
        statusList.add(SysConstant.USER_STATUS_LOCK);
        List<UserInfoDTO> userList = getUserList(null, RoleCodeEnum.DXZJ.name(), statusList);
        Map<Long, UserInfoDTO> userMap = new HashMap<Long, UserInfoDTO>();
        // 生成<机构id，用户>map
        for (UserInfoDTO userInfoDTO : userList) {
            userMap.put(userInfoDTO.getOrgId(), userInfoDTO);
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        // 生成结果集，匹配电销组以及电销总监
        for (OrganizationDTO organizationDTO : data) {
            Map<String, Object> orgMap = new HashMap<String, Object>();
            UserInfoDTO user = userMap.get(organizationDTO.getId());
            orgMap.put("orgId", organizationDTO.getId());
            orgMap.put("orgName", organizationDTO.getName());
            if (user != null) {
                orgMap.put("userId", user.getId());
                orgMap.put("userName", user.getName());
                orgMap.put("id", organizationDTO.getId() + "," + user.getId());
                orgMap.put("name", organizationDTO.getName() + "(" + user.getName() + ")");
                result.add(orgMap);
            }
        }
        return result;
    }

    /**
     * 根据机构和角色类型获取用户
     * 
     * @param orgDTO
     * @return
     */
    private List<UserInfoDTO> getUserList(Long orgId, String roleCode, List<Integer> statusList) {
        UserOrgRoleReq userOrgRoleReq = new UserOrgRoleReq();
        userOrgRoleReq.setOrgId(orgId);
        userOrgRoleReq.setRoleCode(roleCode);
        userOrgRoleReq.setStatusList(statusList);
        JSONResult<List<UserInfoDTO>> listByOrgAndRole =
                userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        return listByOrgAndRole.getData();
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

    /**
     * @Description:处理当前登录业务线是否能复制
     * @Param:
     * @return:
     * @author: fanjd
     * @date: 2020/8/12 14:32
     * @version: V1.0
     */
    private Boolean handleCanCopy(Integer businessLine) {
        // 获取外包业务线
        String wbBusinessLine = getSysSetting(SysConstant.WB_BUSINESSLINE);
        if (null== wbBusinessLine) {
            return  false;
        }
        List<String> wbBusinessLineList = Arrays.asList(wbBusinessLine.split(","));
        if (wbBusinessLineList.contains(String.valueOf(businessLine))) {
            return true;
        }
        return false;
    }
    /**
     * 查询系统参数
     */
    private String getSysSetting(String code) {
        SysSettingReq sysSettingReq = new SysSettingReq();
        sysSettingReq.setCode(code);
        JSONResult<SysSettingDTO> byCode = sysSettingFeignClient.getByCode(sysSettingReq);
        if (byCode != null && JSONResult.SUCCESS.equals(byCode.getCode())) {
            return byCode.getData().getValue();
        }
        return null;
    }
}
