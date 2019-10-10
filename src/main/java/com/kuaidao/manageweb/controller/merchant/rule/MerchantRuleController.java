/**
 *
 */
package com.kuaidao.manageweb.controller.merchant.rule;

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
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.merchant.clue.MerchantClueApplyFeignClient;
import com.kuaidao.manageweb.feign.merchant.rule.MerchantAssignRuleFeignClient;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.user.SysSettingFeignClient;
import com.kuaidao.merchant.dto.clue.MerchantClueApplyDto;
import com.kuaidao.merchant.dto.rule.MerchantAssignRuleDTO;
import com.kuaidao.merchant.dto.rule.MerchantAssignRulePageParam;
import com.kuaidao.merchant.dto.rule.MerchantAssignRuleReq;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.SysSettingDTO;
import com.kuaidao.sys.dto.user.SysSettingReq;
import com.kuaidao.sys.dto.user.UserInfoDTO;

/**
 * @author zxy
 *
 */

@Controller
@RequestMapping("/merchant/merchantAssignRule")
public class MerchantRuleController {
    private static Logger logger = LoggerFactory.getLogger(MerchantRuleController.class);
    @Autowired
    private MerchantAssignRuleFeignClient merchantAssignRuleFeignClient;
    @Autowired
    private MerchantUserInfoFeignClient merchantUserInfoFeignClient;
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;
    @Autowired
    private SysSettingFeignClient sysSettingFeignClient;
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;
    @Autowired
    private MerchantClueApplyFeignClient merchantClueApplyFeignClient;

    /***
     * 商家规则列表页
     *
     * @return
     */
    @RequestMapping("/initRuleList")
    @RequiresPermissions("merchantAssignRule:ruleManager:view")
    public String initCompanyList(HttpServletRequest request) {
        UserInfoDTO user = getUser();

        // 查询资源类别集合
        request.setAttribute("categoryList",
                getDictionaryByCode(DicCodeEnum.CLUECHARGECATEGORY.getCode()));
        // 查询所有项目
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();
        request.setAttribute("projectList", allProject.getData());
        // 当前人员id
        request.setAttribute("userId", user.getId() + "");
        // 当前人员角色code
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null && roleList.size() != 0) {
            request.setAttribute("roleCode", roleList.get(0).getRoleCode());
        }
        // 商家主账号
        List<UserInfoDTO> userList = getMerchantUser(null);
        request.setAttribute("userList", userList);

        return "merchant/rule/merchantRuleManagerPage";
    }

    /***
     * 新增商家规则页
     *
     * @return
     */
    @RequestMapping("/initCreate")
    @RequiresPermissions("merchantAssignRule:ruleManager:add")
    public String initCreateProject(HttpServletRequest request) {
        List<Integer> arrayList = new ArrayList<Integer>();
        arrayList.add(SysConstant.USER_STATUS_ENABLE);
        arrayList.add(SysConstant.USER_STATUS_LOCK);
        // 商家主账号
        List<UserInfoDTO> userList = getMerchantUser(arrayList);
        request.setAttribute("userList", userList);
        // 查询字典资源类别集合
        request.setAttribute("categoryList",
                getDictionaryByCode(DicCodeEnum.CLUECHARGECATEGORY.getCode()));
        // 查询所有项目
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();
        request.setAttribute("projectList", allProject.getData());
        return "merchant/rule/addMerchantRulePage";
    }

    /***
     * 编辑商家规则页
     *
     * @return
     */
    @RequestMapping("/initUpdate")
    @RequiresPermissions("merchantAssignRule:ruleManager:edit")
    public String initUpdateProject(@RequestParam Long id, HttpServletRequest request) {
        // 查询商家规则信息
        JSONResult<MerchantAssignRuleDTO> jsonResult =
                merchantAssignRuleFeignClient.get(new IdEntityLong(id));
        MerchantAssignRuleDTO data = jsonResult.getData();

        request.setAttribute("merchantAssignRule", data);
        // 商家主账号
        List<Integer> arrayList = new ArrayList<Integer>();
        arrayList.add(SysConstant.USER_STATUS_ENABLE);
        arrayList.add(SysConstant.USER_STATUS_LOCK);
        List<UserInfoDTO> userList = getMerchantUser(arrayList);
        request.setAttribute("userList", userList);

        // 查询字典资源类别集合
        request.setAttribute("categoryList",
                getDictionaryByCode(DicCodeEnum.CLUECHARGECATEGORY.getCode()));
        // 查询所有项目
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();
        request.setAttribute("projectList", allProject.getData());
        return "merchant/rule/updateMerchantRulePage";
    }

    /***
     * 商家规则列表
     *
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    @RequiresPermissions("merchantAssignRule:ruleManager:view")
    public JSONResult<PageBean<MerchantAssignRuleDTO>> list(
            @RequestBody MerchantAssignRulePageParam pageParam, HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        pageParam.setUserId(user.getId());

        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {
            pageParam.setRoleCode(roleList.get(0).getRoleCode());
        }
        // 商家规则
        JSONResult<PageBean<MerchantAssignRuleDTO>> list =
                merchantAssignRuleFeignClient.list(pageParam);

        return list;
    }

    /***
     * 商家规则列表
     *
     * @return
     */
    @PostMapping("/get")
    @ResponseBody
    @RequiresPermissions("merchantAssignRule:ruleManager:view")
    public JSONResult<MerchantAssignRuleDTO> list(@RequestBody IdEntityLong idEntity,
            HttpServletRequest request) {
        JSONResult<MerchantAssignRuleDTO> jsonResult = merchantAssignRuleFeignClient.get(idEntity);

        return jsonResult;
    }

    /***
     * 商家最新审批过的申请
     *
     * @return
     */
    @PostMapping("/getPassByUserId")
    @ResponseBody
    public JSONResult<MerchantClueApplyDto> getPassByUserId(@RequestBody IdEntityLong userId,
            HttpServletRequest request) {
        // 商家申请
        JSONResult<MerchantClueApplyDto> passByUserId =
                merchantClueApplyFeignClient.getPassByUserId(userId);
        return passByUserId;
    }


    /**
     * 保存商家规则
     *
     * @param orgDTO
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostMapping("/save")
    @ResponseBody
    @RequiresPermissions("merchantAssignRule:ruleManager:add")
    @LogRecord(description = "新增商家规则", operationType = OperationType.INSERT,
            menuName = MenuEnum.MERCHANT_RULE_MANAGEMENT)
    public JSONResult save(@Valid @RequestBody MerchantAssignRuleReq merchantAssignRuleReq,
            BindingResult result) {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        // 插入创建人信息
        UserInfoDTO user = getUser();
        merchantAssignRuleReq.setCreateUser(user.getId());
        merchantAssignRuleReq.setUpdateUser(user.getId());
        // 插入类型为优化
        return merchantAssignRuleFeignClient.create(merchantAssignRuleReq);
    }

    /**
     * 修改商家规则
     *
     * @param orgDTO
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    @RequiresPermissions("merchantAssignRule:ruleManager:edit")
    @LogRecord(description = "修改商家规则信息", operationType = OperationType.UPDATE,
            menuName = MenuEnum.MERCHANT_RULE_MANAGEMENT)
    public JSONResult update(@Valid @RequestBody MerchantAssignRuleReq merchantAssignRuleReq,
            BindingResult result) {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        // 插入修改人信息
        UserInfoDTO user = getUser();
        merchantAssignRuleReq.setUpdateUser(user.getId());
        Long id = merchantAssignRuleReq.getId();
        if (id == null) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        return merchantAssignRuleFeignClient.update(merchantAssignRuleReq);
    }

    /**
     * 启用规则
     *
     * @param orgDTO
     * @return
     */
    @PostMapping("/updateStatusEnable")
    @ResponseBody
    @RequiresPermissions("merchantAssignRule:ruleManager:edit")
    @LogRecord(description = "启用商家规则", operationType = OperationType.ENABLE,
            menuName = MenuEnum.MERCHANT_RULE_MANAGEMENT)
    public JSONResult updateStatusEnable(
            @Valid @RequestBody MerchantAssignRuleReq merchantAssignRuleReq, BindingResult result) {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }

        Long id = merchantAssignRuleReq.getId();
        if (id == null) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        return merchantAssignRuleFeignClient.updateStatus(merchantAssignRuleReq);
    }

    /**
     * 禁用规则
     *
     * @param orgDTO
     * @return
     */
    @PostMapping("/updateStatusDisable")
    @ResponseBody
    @RequiresPermissions("merchantAssignRule:ruleManager:edit")
    @LogRecord(description = "禁用商家规则", operationType = OperationType.DISABLE,
            menuName = MenuEnum.MERCHANT_RULE_MANAGEMENT)
    public JSONResult updateStatusDisable(
            @Valid @RequestBody MerchantAssignRuleReq merchantAssignRuleReq, BindingResult result) {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        Long id = merchantAssignRuleReq.getId();
        if (id == null) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        return merchantAssignRuleFeignClient.updateStatus(merchantAssignRuleReq);
    }


    /**
     * 删除商家规则
     *
     * @param orgDTO
     * @return
     */
    @PostMapping("/delete")
    @ResponseBody
    @RequiresPermissions("merchantAssignRule:ruleManager:delete")
    @LogRecord(description = "删除规则", operationType = OperationType.DELETE,
            menuName = MenuEnum.MERCHANT_RULE_MANAGEMENT)
    public JSONResult delete(@RequestBody IdListLongReq idList) {
        return merchantAssignRuleFeignClient.delete(idList);
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
     * 查询系统参数
     *
     * @param code
     * @return
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
     * 查询商家主账号
     *
     * @param code
     * @return
     */
    private List<UserInfoDTO> getMerchantUser(List<Integer> arrayList) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUserType(SysConstant.USER_TYPE_TWO);
        userInfoDTO.setStatusList(arrayList);
        JSONResult<List<UserInfoDTO>> merchantUserList =
                merchantUserInfoFeignClient.merchantUserList(userInfoDTO);
        return merchantUserList.getData();
    }
}
