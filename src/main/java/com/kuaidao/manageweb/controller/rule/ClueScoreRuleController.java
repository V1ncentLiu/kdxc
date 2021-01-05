/**
 *
 */
package com.kuaidao.manageweb.controller.rule;

import com.kuaidao.businessconfig.dto.rule.ClueScoreRuleDTO;
import com.kuaidao.businessconfig.dto.rule.UserScoreRuleDTO;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.rule.ClueAssignRuleFeignClient;
import com.kuaidao.manageweb.feign.rule.ClueScoreRuleFeignClient;
import com.kuaidao.manageweb.feign.user.SysSettingFeignClient;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author zxy
 *
 */

@Controller
@RequestMapping("/rule/clueScoreRule")
public class ClueScoreRuleController {
    private static Logger logger = LoggerFactory.getLogger(ClueScoreRuleController.class);
    @Autowired
    private ClueScoreRuleFeignClient clueScoreRuleFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;
    @Autowired
    private SysSettingFeignClient sysSettingFeignClient;
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;

    /***
     * 优化规则列表页
     *
     * @return
     */
    @RequestMapping("/cluesScore")
    public String initCompanyList(HttpServletRequest request) {
        return "assignrule/cluesScore";
    }

    /***
     * 优化规则列表页
     *
     * @return
     */
    @RequestMapping("/updateCluesScor")
    public String updatetelemarketingScore(HttpServletRequest request) {
        return "assignrule/updateCluesScore";
    }
    /***
     * 列表
     *
     * @return.
     */
    @PostMapping("/getClueScoreRuleListByPage")
    @ResponseBody
    @RequiresPermissions("clueAssignRule:clueScoreRuleManager:view")
    public JSONResult<PageBean<ClueScoreRuleDTO>> getClueScoreRuleListByPage(
            @RequestBody ClueScoreRuleDTO pageParam, HttpServletRequest request) {
        JSONResult<PageBean<ClueScoreRuleDTO>> list = clueScoreRuleFeignClient.getClueScoreRuleListByPage(pageParam);
        return list;
    }
    /**
     * 保存规则
     *
     * @param
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostMapping("/save")
    @ResponseBody
    @RequiresPermissions("clueAssignRule:clueScoreRuleManager:add")
    @LogRecord(description = "新增资源得分规则", operationType = LogRecord.OperationType.INSERT,
            menuName = MenuEnum.CLUE_SCORE_RULE_MANAGEMENT)
    public JSONResult save(@Valid @RequestBody ClueScoreRuleDTO clueScoreRuleDTO,
                           BindingResult result) {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        // 插入创建人信息
        UserInfoDTO user = getUser();
        clueScoreRuleDTO.setCreateUser(user.getId());
        return clueScoreRuleFeignClient.create(clueScoreRuleDTO);
    }
    /**
     * 获取当前登录账号
     *
     * @param
     * @return
     */
    private UserInfoDTO getUser() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user;
    }
    /**
     * 规则
     *
     * @param
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostMapping("/getRuleByBusinessLine")
    @ResponseBody
    public JSONResult<Boolean> getRuleByBusinessLine(@Valid @RequestBody ClueScoreRuleDTO clueScoreRuleDTO,
                                                     BindingResult result) {
        return clueScoreRuleFeignClient.getRuleByBusinessLine(clueScoreRuleDTO);
    }
    /**
     * 删除非优化规则
     *
     * @param
     * @return
     */
    @PostMapping("/delete")
    @ResponseBody
    @RequiresPermissions("clueAssignRule:clueScoreRuleManager:delete")
    @LogRecord(description = "删除资源得分规则", operationType = LogRecord.OperationType.DELETE,
            menuName = MenuEnum.CLUE_SCORE_RULE_MANAGEMENT)
    public JSONResult delete(@RequestBody IdListLongReq idList) {
        return clueScoreRuleFeignClient.delete(idList);
    }
    /**
     * 规则详情
     *
     * @param
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostMapping("/getRuleDetailById")
    @ResponseBody
    public JSONResult getRuleDetailById(@Valid @RequestBody ClueScoreRuleDTO clueScoreRuleDTO,
                                        BindingResult result) {
        return clueScoreRuleFeignClient.getRuleDetailById(clueScoreRuleDTO);
    }

    /**
     * 规则详情
     *
     * @param
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostMapping("/getBusinesslineList")
    @ResponseBody
    public List<DictionaryItemRespDTO> getBusinesslineList() {
        // 查询优化类资源类别集合
        return  getDictionaryByCode(DicCodeEnum.BUSINESS_LINE.getCode());
    }

    /**
     * 启用规则
     *
     * @param
     * @return
     */
    @PostMapping("/updateStatusEnable")
    @ResponseBody
    @RequiresPermissions("clueAssignRule:clueScoreRuleManager:edit")
    @LogRecord(description = "启用资源得分规则", operationType = LogRecord.OperationType.ENABLE,
            menuName = MenuEnum.CLUE_SCORE_RULE_MANAGEMENT)
    public JSONResult updateStatusEnable(@Valid @RequestBody ClueScoreRuleDTO clueScoreRuleDTO,
                                         BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }

        Long id = clueScoreRuleDTO.getId();
        if (id == null) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        UserInfoDTO user = getUser();
        clueScoreRuleDTO.setUpdateUser(user.getId());
        return clueScoreRuleFeignClient.updateStatus(clueScoreRuleDTO);
    }

    /**
     * 禁用规则
     *
     * @param
     * @return
     */
    @PostMapping("/updateStatusDisable")
    @ResponseBody
    @RequiresPermissions("clueAssignRule:clueScoreRuleManager:edit")
    @LogRecord(description = "禁用资源得分规则", operationType = LogRecord.OperationType.DISABLE,
            menuName = MenuEnum.CLUE_SCORE_RULE_MANAGEMENT)
    public JSONResult updateStatusDisable(@Valid @RequestBody ClueScoreRuleDTO clueScoreRuleDTO,
                                          BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }

        Long id = clueScoreRuleDTO.getId();
        if (id == null) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        UserInfoDTO user = getUser();
        clueScoreRuleDTO.setUpdateUser(user.getId());
        return clueScoreRuleFeignClient.updateStatus(clueScoreRuleDTO);
    }
    /**
     * 修改非优化规则
     *
     * @param
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    @RequiresPermissions("clueAssignRule:clueScoreRuleManager:edit")
    @LogRecord(description = "修改资源得分规则信息", operationType = LogRecord.OperationType.UPDATE,
            menuName = MenuEnum.CLUE_SCORE_RULE_MANAGEMENT)
    public JSONResult update(@Valid @RequestBody ClueScoreRuleDTO clueScoreRuleDTO,
                             BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        // 插入修改人信息
        UserInfoDTO user = getUser();
        clueScoreRuleDTO.setUpdateUser(user.getId());
        Long id = clueScoreRuleDTO.getId();
        if (id == null) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        return clueScoreRuleFeignClient.update(clueScoreRuleDTO);
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
