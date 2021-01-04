/**
 *
 */
package com.kuaidao.manageweb.controller.rule;

import com.kuaidao.businessconfig.dto.rule.*;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.rule.UserScoreRuleFeignClient;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author zxy
 *
 */

@Controller
@RequestMapping("/rule/userScoreRule")
public class UserScoreRuleController {
    private static Logger logger = LoggerFactory.getLogger(UserScoreRuleController.class);
    @Autowired
    UserScoreRuleFeignClient userScoreRuleFeignClient;
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;

    /***
     * 优化规则列表页
     *
     * @return
     */
    @RequestMapping("/userScoreRulePage")
    public String userScoreRulePage(HttpServletRequest request) {
        return "rule/userScoreRulePage";
    }

    /***
     * 优化规则列表页
     *
     * @return
     */
    @RequestMapping("/addtelemarketingScore")
    @RequiresPermissions("clueAssignRule:userScoreRuleManager:add")
    public String addtelemarketingScore(HttpServletRequest request) {
        return "rule/updatetelemarketingScore";
    }
    /***
     * 优化规则列表页
     *
     * @return
     */
    @RequestMapping("/updatetelemarketingScore")
    @RequiresPermissions("clueAssignRule:userScoreRuleManager:edit")
    public String updatetelemarketingScore(HttpServletRequest request)
    {
        return "rule/updatetelemarketingScore";
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
    /***
     * 列表
     *
     * @return
     */
    @PostMapping("/userScoreRuleList")
    @ResponseBody
    @RequiresPermissions("clueAssignRule:userScoreRuleManager:view")
    public JSONResult<PageBean<UserScoreRuleDTO>> getUserScoreRuleListByPage(
            @RequestBody UserScoreRuleDTO pageParam, HttpServletRequest request) {
        JSONResult<PageBean<UserScoreRuleDTO>> list = userScoreRuleFeignClient.getUserScoreRuleListByPage(pageParam);
        return list;
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
     * 保存规则
     *
     * @param
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostMapping("/save")
    @ResponseBody
    @RequiresPermissions("clueAssignRule:userScoreRuleManager:add")
    @LogRecord(description = "新增顾问得分规则", operationType = LogRecord.OperationType.INSERT,
            menuName = MenuEnum.USER_SCORE_RULE_MANAGEMENT)
    public JSONResult save(@Valid @RequestBody UserScoreRuleDTO userScoreRuleDTO,
                           BindingResult result) {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        // 插入创建人信息
        UserInfoDTO user = getUser();
        userScoreRuleDTO.setCreateUser(user.getId());
        return userScoreRuleFeignClient.create(userScoreRuleDTO);
    }

    /**
     * 保存规则
     *
     * @param
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostMapping("/getRuleByBusinessLine")
    @ResponseBody
    public JSONResult getRuleByBusinessLine(@Valid @RequestBody UserScoreRuleDTO userScoreRuleDTO,
                                            BindingResult result) {
        return userScoreRuleFeignClient.getRuleByBusinessLine(userScoreRuleDTO);
    }

    /**
     * 删除非优化规则
     *
     * @param
     * @return
     */
    @PostMapping("/delete")
    @ResponseBody
    @RequiresPermissions("clueAssignRule:userScoreRuleManager:delete")
    @LogRecord(description = "删除规则", operationType = LogRecord.OperationType.DELETE,
            menuName = MenuEnum.USER_SCORE_RULE_MANAGEMENT)
    public JSONResult delete(@RequestBody IdListLongReq idList) {
        return userScoreRuleFeignClient.delete(idList);
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
    public JSONResult getRuleDetailById(@Valid @RequestBody UserScoreRuleDTO userScoreRuleDTO,
                                        BindingResult result) {
        return userScoreRuleFeignClient.getRuleDetailById(userScoreRuleDTO);
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
        return  new getDictionaryByCode(DicCodeEnum.BUSINESS_LINE.getCode());
    }
}
