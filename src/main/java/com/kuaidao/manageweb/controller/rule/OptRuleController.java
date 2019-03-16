/**
 * 
 */
package com.kuaidao.manageweb.controller.rule;

import java.lang.reflect.InvocationTargetException;
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
import com.kuaidao.aggregation.constant.AggregationConstant;
import com.kuaidao.aggregation.dto.rule.ClueAssignRuleDTO;
import com.kuaidao.aggregation.dto.rule.ClueAssignRulePageParam;
import com.kuaidao.aggregation.dto.rule.ClueAssignRuleReq;
import com.kuaidao.common.constant.OrgTypeConstant;
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
import com.kuaidao.manageweb.feign.area.SysRegionFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.rule.ClueAssignRuleFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;

/**
 * @author zxy
 *
 */

@Controller
@RequestMapping("/clueAssignRule/optRule")
public class OptRuleController {
    private static Logger logger = LoggerFactory.getLogger(OptRuleController.class);
    @Autowired
    private ClueAssignRuleFeignClient clueAssignRuleFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;
    @Autowired
    private SysRegionFeignClient sysRegionFeignClient;

    /***
     * 优化规则列表页
     * 
     * @return
     */
    @RequestMapping("/initRuleList")
    @RequiresPermissions("clueAssignRule:optRuleManager:view")
    public String initCompanyList(HttpServletRequest request) {
        // 查询字典资源类别集合
        request.setAttribute("clueCategoryList", getDictionaryByCode(Constants.CLUE_CATEGORY));
        // 查询字典行业类别集合
        request.setAttribute("industryCategoryList",
                getDictionaryByCode(Constants.INDUSTRY_CATEGORY));
        // 查询字典媒介集合
        request.setAttribute("mediumList", getDictionaryByCode(Constants.MEDIUM));
        return "rule/optRuleManagerPage";
    }

    /***
     * 新增优化规则页
     * 
     * @return
     */
    @RequestMapping("/initCreate")
    @RequiresPermissions("clueAssignRule:optRuleManager:add")
    public String initCreateProject(HttpServletRequest request) {
        // 查询电销组
        OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
        organizationQueryDTO.setOrgType(OrgTypeConstant.DXZ);
        JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                organizationFeignClient.queryOrgByParam(organizationQueryDTO);
        request.setAttribute("orgList", queryOrgByParam.getData());
        // 查询字典资源类别集合
        request.setAttribute("clueCategoryList", getDictionaryByCode(Constants.CLUE_CATEGORY));
        // 查询字典行业类别集合
        request.setAttribute("industryCategoryList",
                getDictionaryByCode(Constants.INDUSTRY_CATEGORY));
        // 查询字典媒介集合
        request.setAttribute("mediumList", getDictionaryByCode(Constants.MEDIUM));
        return "rule/addOptRulePage";
    }

    /***
     * 编辑优化规则页
     * 
     * @return
     */
    @RequestMapping("/initUpdate")
    @RequiresPermissions("clueAssignRule:optRuleManager:edit")
    public String initUpdateProject(@RequestParam Long id, HttpServletRequest request) {
        // 查询优化规则信息
        JSONResult<ClueAssignRuleDTO> jsonResult =
                clueAssignRuleFeignClient.get(new IdEntityLong(id));
        request.setAttribute("clueAssignRule", jsonResult.getData());
        // 查询电销组
        OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
        organizationQueryDTO.setOrgType(OrgTypeConstant.DXZ);
        JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                organizationFeignClient.queryOrgByParam(organizationQueryDTO);
        request.setAttribute("orgList", queryOrgByParam.getData());
        // 查询字典资源类别集合
        request.setAttribute("clueCategoryList", getDictionaryByCode(Constants.CLUE_CATEGORY));
        // 查询字典行业类别集合
        request.setAttribute("industryCategoryList",
                getDictionaryByCode(Constants.INDUSTRY_CATEGORY));
        // 查询字典媒介集合
        request.setAttribute("mediumList", getDictionaryByCode(Constants.MEDIUM));
        return "rule/updateOptRulePage";
    }

    /***
     * 优化规则列表
     * 
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    @RequiresPermissions("clueAssignRule:optRuleManager:view")
    public JSONResult<PageBean<ClueAssignRuleDTO>> list(
            @RequestBody ClueAssignRulePageParam pageParam, HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        pageParam.setUserId(user.getId());
        pageParam.setOrgId(user.getOrgId());

        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {

            pageParam.setRoleCode(roleList.get(0).getRoleCode());
        }
        // 优化规则
        pageParam.setRuleType(AggregationConstant.RULE_TYPE.OPT);
        JSONResult<PageBean<ClueAssignRuleDTO>> list = clueAssignRuleFeignClient.list(pageParam);

        return list;
    }


    /**
     * 保存优化规则
     * 
     * @param orgDTO
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostMapping("/save")
    @ResponseBody
    @RequiresPermissions("clueAssignRule:optRuleManager:add")
    @LogRecord(description = "新增优化规则", operationType = OperationType.INSERT,
            menuName = MenuEnum.OPT_RULE_MANAGEMENT)
    public JSONResult save(@Valid @RequestBody ClueAssignRuleReq clueAssignRuleReq,
            BindingResult result) {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        // 插入创建人信息
        UserInfoDTO user = getUser();
        clueAssignRuleReq.setCreateUser(user.getId());
        clueAssignRuleReq.setOrgId(user.getOrgId());
        // 插入类型为优化
        clueAssignRuleReq.setRuleType(AggregationConstant.RULE_TYPE.OPT);
        return clueAssignRuleFeignClient.create(clueAssignRuleReq);
    }

    /**
     * 修改优化规则
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    @RequiresPermissions("clueAssignRule:optRuleManager:edit")
    @LogRecord(description = "修改优化规则信息", operationType = OperationType.UPDATE,
            menuName = MenuEnum.OPT_RULE_MANAGEMENT)
    public JSONResult update(@Valid @RequestBody ClueAssignRuleReq clueAssignRuleReq,
            BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }

        Long id = clueAssignRuleReq.getId();
        if (id == null) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        return clueAssignRuleFeignClient.update(clueAssignRuleReq);
    }

    /**
     * 启用规则
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/updateStatusEnable")
    @ResponseBody
    @RequiresPermissions("clueAssignRule:optRuleManager:edit")
    @LogRecord(description = "启用优化规则", operationType = OperationType.ENABLE,
            menuName = MenuEnum.OPT_RULE_MANAGEMENT)
    public JSONResult updateStatusEnable(@Valid @RequestBody ClueAssignRuleReq clueAssignRuleReq,
            BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }

        Long id = clueAssignRuleReq.getId();
        if (id == null) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        return clueAssignRuleFeignClient.updateStatus(clueAssignRuleReq);
    }

    /**
     * 禁用规则
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/updateStatusDisable")
    @ResponseBody
    @RequiresPermissions("clueAssignRule:optRuleManager:edit")
    @LogRecord(description = "禁用优化规则", operationType = OperationType.DISABLE,
            menuName = MenuEnum.OPT_RULE_MANAGEMENT)
    public JSONResult updateStatusDisable(@Valid @RequestBody ClueAssignRuleReq clueAssignRuleReq,
            BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }

        Long id = clueAssignRuleReq.getId();
        if (id == null) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        return clueAssignRuleFeignClient.updateStatus(clueAssignRuleReq);
    }


    /**
     * 删除优化规则
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/delete")
    @ResponseBody
    @RequiresPermissions("clueAssignRule:optRuleManager:delete")
    @LogRecord(description = "删除规则", operationType = OperationType.DELETE,
            menuName = MenuEnum.OPT_RULE_MANAGEMENT)
    public JSONResult delete(@RequestBody IdListLongReq idList) {

        return clueAssignRuleFeignClient.delete(idList);
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
