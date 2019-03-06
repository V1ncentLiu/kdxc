/**
 * 
 */
package com.kuaidao.manageweb.controller.clue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
import com.kuaidao.aggregation.dto.assignrule.InfoAssignDTO;
import com.kuaidao.aggregation.dto.assignrule.InfoAssignQueryDTO;
import com.kuaidao.aggregation.dto.clue.AllocationClueReq;
import com.kuaidao.aggregation.dto.clue.PendingAllocationClueDTO;
import com.kuaidao.aggregation.dto.clue.PendingAllocationCluePageParam;
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

    /***
     * 待分配资源列表页
     * 
     * @return
     */
    @RequestMapping("/initAppiontmentList")
    @RequiresPermissions("aggregation:pendingAllocationManager:view")
    public String initCompanyList(HttpServletRequest request) {
        UserInfoDTO user = getUser();
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null && RoleCodeEnum.DXZJ.name().equals(roleList.get(0).getRoleCode())) {
            // 如果当前登录的为电销总监,查询所有下属电销员工
            List<Integer> statusList = new ArrayList<Integer>();
            statusList.add(SysConstant.USER_STATUS_ENABLE);
            statusList.add(SysConstant.USER_STATUS_LOCK);
            List<UserInfoDTO> userList =
                    getUserList(user.getOrgId(), RoleCodeEnum.DXCYGW.name(), statusList);
            request.setAttribute("saleList", userList);
            // 查询同事业部下的电销组
            Long orgId = user.getOrgId();
            JSONResult<OrganizationDTO> queryOrgById =
                    organizationFeignClient.queryOrgById(new IdEntity(orgId.toString()));
            List<Map<String, Object>> saleGroupList =
                    getSaleGroupList(queryOrgById.getData().getParentId());
            request.setAttribute("saleGroupList", saleGroupList);


        } else if (roleList != null
                && RoleCodeEnum.DXFZ.name().equals(roleList.get(0).getRoleCode())) {
            // 如果当前登录的为电销副总,查询所有下属电销组
            List<Map<String, Object>> saleGroupList = getSaleGroupList(user.getOrgId());
            request.setAttribute("orgList", saleGroupList);
        }
        // 查询所有信息流分配规则
        InfoAssignQueryDTO infoAssignQueryDTO = new InfoAssignQueryDTO();
        infoAssignQueryDTO.setPageNum(1);
        infoAssignQueryDTO.setPageSize(1000);
        JSONResult<PageBean<InfoAssignDTO>> pageBean =
                infoAssignFeignClient.queryInfoAssignPage(infoAssignQueryDTO);
        request.setAttribute("ruleList", pageBean.getData().getData());
        // 查询字典类别集合
        request.setAttribute("clueCategoryList", getDictionaryByCode(Constants.CLUE_CATEGORY));
        // 查询字典类别集合
        request.setAttribute("clueTypeList", getDictionaryByCode(Constants.CLUE_TYPE));

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
        queryFieldByUserAndMenuReq.setMenuCode("aggregation:pendingAllocationManager");
        JSONResult<List<UserFieldDTO>> queryFieldByUserAndMenu =
                customFieldFeignClient.queryFieldByUserAndMenu(queryFieldByUserAndMenuReq);
        request.setAttribute("userFieldList", queryFieldByUserAndMenu.getData());

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
            menuName = MenuEnum.PENDING_ALLOCATION_CLUE)
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
    private List<Map<String, Object>> getSaleGroupList(Long orgId) {
        OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
        organizationQueryDTO.setParentId(orgId);
        organizationQueryDTO.setOrgType(OrgTypeConstant.DXZ);
        // 查询下级电销组
        JSONResult<List<OrganizationDTO>> listDescenDantByParentId =
                organizationFeignClient.listDescenDantByParentId(organizationQueryDTO);
        List<OrganizationDTO> data = listDescenDantByParentId.getData();
        // 查询所有电销总监
        List<UserInfoDTO> userList = getUserList(null, RoleCodeEnum.DXZJ.name(), null);
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
            orgMap.put("userId", user.getId());
            orgMap.put("userName", user.getName());
            orgMap.put("id", organizationDTO.getId() + "," + user.getId());
            orgMap.put("name", organizationDTO.getName() + "(" + user.getName() + ")");
            result.add(orgMap);
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

}
