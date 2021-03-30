/**
 * 
 */
package com.kuaidao.manageweb.controller.traffcrule;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kuaidao.businessconfig.constant.BusinessConfigConstant;
import com.kuaidao.businessconfig.dto.traffcrule.TrafficAssignRuleDTO;
import com.kuaidao.businessconfig.dto.traffcrule.TrafficAssignRulePageParam;
import com.kuaidao.businessconfig.dto.traffcrule.TrafficAssignRuleReq;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.traffcrule.TrafficRuleFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

/**
 * @author zxy
 */

@Controller
@RequestMapping("/trafficAssignRule/rule")
public class TrafficRuleController {
    private static Logger logger = LoggerFactory.getLogger(TrafficRuleController.class);
    @Autowired
    private TrafficRuleFeignClient trafficRuleFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    /***
     * 话务分配规则列表页
     * @return
     */
    @RequestMapping("/initRuleList")
    @RequiresPermissions("trafficAssignRule:ruleManager:view")
    public String initCompanyList(HttpServletRequest request) {
        // 查询优化类资源类别集合
        request.setAttribute("clueCategoryList", getDictionaryByCode(Constants.CLUE_CATEGORY));
        // 查询字典资源类型集合
        request.setAttribute("clueTypeList", getDictionaryByCode(Constants.CLUE_TYPE));
        return "trafficrule/ruleManagerPage";
    }

    /***
     * 新增话务分配规则页
     * @return
     */
    @RequestMapping("/initCreate")
    @RequiresPermissions("trafficAssignRule:ruleManager:add")
    public String initCreateProject(HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 查询话务组下话务员工
        List<Integer> statusList = new ArrayList<Integer>();
        statusList.add(SysConstant.USER_STATUS_ENABLE);
        statusList.add(SysConstant.USER_STATUS_LOCK);
        List<UserInfoDTO> userList = getUserList(user.getOrgId(), RoleCodeEnum.HWY.name(), statusList);
        request.setAttribute("trafficList", userList);
        // 查询优化类资源类别集合
        request.setAttribute("clueCategoryList", getDictionaryByCode(Constants.CLUE_CATEGORY));
        // 查询字典资源类型集合
        request.setAttribute("clueTypeList", getDictionaryByCode(Constants.CLUE_TYPE));
        return "trafficrule/addRulePage";
    }

    /***
     * 编辑话务分配规则页
     * @return
     */
    @RequestMapping("/initUpdate")
    @RequiresPermissions("trafficAssignRule:ruleManager:edit")
    public String initUpdateProject(@RequestParam Long id, HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 查询话务分配规则信息
        JSONResult<TrafficAssignRuleDTO> jsonResult = trafficRuleFeignClient.get(new IdEntityLong(id));
        request.setAttribute("trafficAssignRule", jsonResult.getData());
        // 查询话务组下话务员工
        List<Integer> statusList = new ArrayList<Integer>();
        statusList.add(SysConstant.USER_STATUS_ENABLE);
        statusList.add(SysConstant.USER_STATUS_LOCK);
        List<UserInfoDTO> userList = getUserList(user.getOrgId(), RoleCodeEnum.HWY.name(), statusList);
        request.setAttribute("trafficList", userList);
        // 查询优化类资源类别集合
        request.setAttribute("clueCategoryList", getDictionaryByCode(Constants.CLUE_CATEGORY));
        // 查询字典资源类型集合
        request.setAttribute("clueTypeList", getDictionaryByCode(Constants.CLUE_TYPE));
        return "trafficrule/updateRulePage";
    }

    /***
     * 话务分配规则列表
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    @RequiresPermissions("trafficAssignRule:ruleManager:view")
    public JSONResult<PageBean<TrafficAssignRuleDTO>> list(@RequestBody TrafficAssignRulePageParam pageParam, HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        pageParam.setUserId(user.getId());

        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {

            pageParam.setRoleCode(roleList.get(0).getRoleCode());
        }
        pageParam.setRuleType(BusinessConfigConstant.TRAFFIC_RULE_TYPE.TRAFFIC);
        // 话务分配规则
        JSONResult<PageBean<TrafficAssignRuleDTO>> list = trafficRuleFeignClient.list(pageParam);

        return list;
    }

    /**
     * 保存话务分配规则
     * @param orgDTO
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostMapping("/save")
    @ResponseBody
    @RequiresPermissions("trafficAssignRule:ruleManager:add")
    @LogRecord(description = "新增话务分配规则", operationType = OperationType.INSERT, menuName = MenuEnum.TRAFFIC_RULE_MANAGEMENT)
    public JSONResult save(@Valid @RequestBody TrafficAssignRuleReq trafficAssignRuleReq, BindingResult result) {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        // 插入创建人信息
        UserInfoDTO user = getUser();
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList.size() > 0) {
            // 插入话务组id
            trafficAssignRuleReq.setTrafficId(user.getOrgId());
        }
        trafficAssignRuleReq.setCreateUser(user.getId());
        trafficAssignRuleReq.setRuleType(BusinessConfigConstant.TRAFFIC_RULE_TYPE.TRAFFIC);
        // 插入类型为优化
        return trafficRuleFeignClient.create(trafficAssignRuleReq);
    }

    /**
     * 修改话务分配规则
     * @param orgDTO
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    @RequiresPermissions("trafficAssignRule:ruleManager:edit")
    @LogRecord(description = "修改话务分配规则信息", operationType = OperationType.UPDATE, menuName = MenuEnum.TRAFFIC_RULE_MANAGEMENT)
    public JSONResult update(@Valid @RequestBody TrafficAssignRuleReq trafficAssignRuleReq, BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }

        Long id = trafficAssignRuleReq.getId();
        if (id == null) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(), SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        return trafficRuleFeignClient.update(trafficAssignRuleReq);
    }

    /**
     * 启用规则
     * @param orgDTO
     * @return
     */
    @PostMapping("/updateStatusEnable")
    @ResponseBody
    @RequiresPermissions("trafficAssignRule:ruleManager:edit")
    @LogRecord(description = "启用话务分配规则", operationType = OperationType.ENABLE, menuName = MenuEnum.TRAFFIC_RULE_MANAGEMENT)
    public JSONResult updateStatusEnable(@Valid @RequestBody TrafficAssignRuleReq trafficAssignRuleReq, BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }

        Long id = trafficAssignRuleReq.getId();
        if (id == null) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(), SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        return trafficRuleFeignClient.updateStatus(trafficAssignRuleReq);
    }

    /**
     * 禁用规则
     * @param orgDTO
     * @return
     */
    @PostMapping("/updateStatusDisable")
    @ResponseBody
    @RequiresPermissions("trafficAssignRule:ruleManager:edit")
    @LogRecord(description = "禁用话务分配规则", operationType = OperationType.DISABLE, menuName = MenuEnum.TRAFFIC_RULE_MANAGEMENT)
    public JSONResult updateStatusDisable(@Valid @RequestBody TrafficAssignRuleReq trafficAssignRuleReq, BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }

        Long id = trafficAssignRuleReq.getId();
        if (id == null) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(), SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        return trafficRuleFeignClient.updateStatus(trafficAssignRuleReq);
    }

    /**
     * 删除话务分配规则
     * @param orgDTO
     * @return
     */
    @PostMapping("/delete")
    @ResponseBody
    @RequiresPermissions("trafficAssignRule:ruleManager:delete")
    @LogRecord(description = "删除规则", operationType = OperationType.DELETE, menuName = MenuEnum.TRAFFIC_RULE_MANAGEMENT)
    public JSONResult delete(@RequestBody IdListLongReq idList) {

        return trafficRuleFeignClient.delete(idList);
    }

    /**
     * 获取当前登录账号
     * @param orgDTO
     * @return
     */
    private UserInfoDTO getUser() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user;
    }

    /**
     * 查询字典表
     * @param code
     * @return
     */
    private List<DictionaryItemRespDTO> getDictionaryByCode(String code) {
        JSONResult<List<DictionaryItemRespDTO>> queryDicItemsByGroupCode = dictionaryItemFeignClient.queryDicItemsByGroupCode(code);
        if (queryDicItemsByGroupCode != null && JSONResult.SUCCESS.equals(queryDicItemsByGroupCode.getCode())) {
            return queryDicItemsByGroupCode.getData();
        }
        return null;
    }

    /**
     * 根据机构和角色类型获取用户
     * @param orgDTO
     * @return
     */
    private List<UserInfoDTO> getUserList(Long orgId, String roleCode, List<Integer> statusList) {
        UserOrgRoleReq userOrgRoleReq = new UserOrgRoleReq();
        userOrgRoleReq.setOrgId(orgId);
        userOrgRoleReq.setRoleCode(roleCode);
        userOrgRoleReq.setStatusList(statusList);
        JSONResult<List<UserInfoDTO>> listByOrgAndRole = userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        return listByOrgAndRole.getData();
    }
}
