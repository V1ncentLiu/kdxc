/**
 * 
 */
package com.kuaidao.manageweb.controller.rule;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
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
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
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
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.rule.ClueAssignRuleFeignClient;
import com.kuaidao.manageweb.feign.user.SysSettingFeignClient;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.SysSettingDTO;
import com.kuaidao.sys.dto.user.SysSettingReq;
import com.kuaidao.sys.dto.user.UserInfoDTO;

/**
 * @author zxy
 *
 */

@Controller
@RequestMapping("/clueAssignRule/notOptRule")
public class NotOptRuleController {
    private static Logger logger = LoggerFactory.getLogger(NotOptRuleController.class);
    @Autowired
    private ClueAssignRuleFeignClient clueAssignRuleFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;
    @Autowired
    private SysSettingFeignClient sysSettingFeignClient;

    /***
     * 非优化规则列表页
     * 
     * @return
     */
    @RequestMapping("/initRuleList")
    @RequiresPermissions("clueAssignRule:notOptRuleManager:view")
    public String initCompanyList(HttpServletRequest request) {
        // 查询所有项目
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();
        request.setAttribute("projectList", allProject.getData());

        // 查询非优化字典资源类别集合
        request.setAttribute("clueCategoryList", getNotOptCategory());
        // 查询字典资源类型集合
        request.setAttribute("clueTypeList", getDictionaryByCode(Constants.CLUE_TYPE));
        // 查询字典广告位集合
        request.setAttribute("adsenseList", getDictionaryByCode(Constants.ADSENSE));
        // 查询字典媒介集合
        request.setAttribute("mediumList", getDictionaryByCode(Constants.MEDIUM));
        return "rule/notOptRuleManagerPage";
    }

    /***
     * 新增非优化规则页
     * 
     * @return
     */
    @RequestMapping("/initCreate")
    @RequiresPermissions("clueAssignRule:notOptRuleManager:add")
    public String initCreateProject(HttpServletRequest request) {
        // 查询话务组
        request.setAttribute("trafficList", getTrafficGroup());
        JSONResult<List<OrganizationDTO>> listBusinessLineOrg =
                organizationFeignClient.listBusinessLineOrg();
        // 查询所有业务线
        request.setAttribute("businessLineList", listBusinessLineOrg.getData());
        // 查询所有项目
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();
        request.setAttribute("projectList", allProject.getData());
        // 查询非优化字典资源类别集合
        request.setAttribute("clueCategoryList", getNotOptCategory());
        // 查询字典资源类型集合
        request.setAttribute("clueTypeList", getDictionaryByCode(Constants.CLUE_TYPE));
        // 查询字典广告位集合
        request.setAttribute("adsenseList", getDictionaryByCode(Constants.ADSENSE));
        // 查询字典媒介集合
        request.setAttribute("mediumList", getDictionaryByCode(Constants.MEDIUM));
        return "rule/addNotOptRulePage";
    }

    /***
     * 编辑非优化规则页
     * 
     * @return
     */
    @RequestMapping("/initUpdate")
    @RequiresPermissions("clueAssignRule:notOptRuleManager:edit")
    public String initUpdateProject(@RequestParam Long id, HttpServletRequest request) {
        // 查询非优化规则信息
        JSONResult<ClueAssignRuleDTO> jsonResult =
                clueAssignRuleFeignClient.get(new IdEntityLong(id));
        request.setAttribute("clueAssignRule", jsonResult.getData());
        // 查询话务组
        request.setAttribute("trafficList", getTrafficGroup());
        JSONResult<List<OrganizationDTO>> listBusinessLineOrg =
                organizationFeignClient.listBusinessLineOrg();
        // 查询所有业务线
        request.setAttribute("businessLineList", listBusinessLineOrg.getData());
        // 查询所有项目
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();
        request.setAttribute("projectList", allProject.getData());
        // 查询非优化字典资源类别集合
        request.setAttribute("clueCategoryList", getNotOptCategory());
        // 查询字典资源类型集合
        request.setAttribute("clueTypeList", getDictionaryByCode(Constants.CLUE_TYPE));
        // 查询字典广告位集合
        request.setAttribute("adsenseList", getDictionaryByCode(Constants.ADSENSE));
        // 查询字典媒介集合
        request.setAttribute("mediumList", getDictionaryByCode(Constants.MEDIUM));
        return "rule/updateNotOptRulePage";
    }

    /***
     * 非优化规则列表
     * 
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    @RequiresPermissions("clueAssignRule:notOptRuleManager:view")
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
        // 非优化规则
        pageParam.setRuleType(AggregationConstant.RULE_TYPE.NOT_OPT);
        JSONResult<PageBean<ClueAssignRuleDTO>> list = clueAssignRuleFeignClient.list(pageParam);

        return list;
    }


    /**
     * 保存非优化规则
     * 
     * @param orgDTO
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostMapping("/save")
    @ResponseBody
    @RequiresPermissions("clueAssignRule:notOptRuleManager:add")
    @LogRecord(description = "新增非优化规则", operationType = OperationType.INSERT,
            menuName = MenuEnum.NOT_OPT_RULE_MANAGEMENT)
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
        clueAssignRuleReq.setRuleType(AggregationConstant.RULE_TYPE.NOT_OPT);
        return clueAssignRuleFeignClient.create(clueAssignRuleReq);
    }

    /**
     * 修改非优化规则
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    @RequiresPermissions("clueAssignRule:notOptRuleManager:edit")
    @LogRecord(description = "修改非优化规则信息", operationType = OperationType.UPDATE,
            menuName = MenuEnum.NOT_OPT_RULE_MANAGEMENT)
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
    @RequiresPermissions("clueAssignRule:notOptRuleManager:edit")
    @LogRecord(description = "启用非优化规则", operationType = OperationType.ENABLE,
            menuName = MenuEnum.NOT_OPT_RULE_MANAGEMENT)
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
    @RequiresPermissions("clueAssignRule:notOptRuleManager:edit")
    @LogRecord(description = "禁用非优化规则", operationType = OperationType.DISABLE,
            menuName = MenuEnum.NOT_OPT_RULE_MANAGEMENT)
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
     * 删除非优化规则
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/delete")
    @ResponseBody
    @RequiresPermissions("clueAssignRule:notOptRuleManager:delete")
    @LogRecord(description = "删除规则", operationType = OperationType.DELETE,
            menuName = MenuEnum.NOT_OPT_RULE_MANAGEMENT)
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
     * 查询系统参数非优化资源类别
     * 
     * @param code
     * @return
     */
    private List<DictionaryItemRespDTO> getNotOptCategory() {
        // 系统参数非优化资源类别
        String reminderTime = getSysSetting(SysConstant.NOPT_CATEGORY);
        List<DictionaryItemRespDTO> dictionaryByCode = getDictionaryByCode(Constants.CLUE_CATEGORY);
        List<DictionaryItemRespDTO> notOptCategory = new ArrayList<DictionaryItemRespDTO>();
        if (StringUtils.isNoneBlank(reminderTime) && dictionaryByCode != null) {
            String[] split = reminderTime.split(",");
            for (DictionaryItemRespDTO dictionaryItemRespDTO : dictionaryByCode) {
                for (int i = 0; i < split.length; i++) {
                    if (split[i].equals(dictionaryItemRespDTO.getValue())) {
                        notOptCategory.add(dictionaryItemRespDTO);
                        continue;
                    }
                }
            }
        }
        return notOptCategory;
    }

    /***
     * 查询电销组加 话务组的集合
     * 
     * @return
     */
    private List<OrganizationRespDTO> getTrafficGroup() {
        OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
        organizationQueryDTO.setOrgType(OrgTypeConstant.HWZ);
        JSONResult<List<OrganizationRespDTO>> trafficResult =
                organizationFeignClient.queryOrgByParam(organizationQueryDTO);
        return trafficResult.getData();
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
