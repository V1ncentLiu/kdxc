/**
 *
 */
package com.kuaidao.manageweb.controller.rule;

import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.rule.ClueAssignRuleFeignClient;
import com.kuaidao.manageweb.feign.user.SysSettingFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zxy
 *
 */

@Controller
@RequestMapping("/rule/clueScoreRule")
public class ClueScoreRuleController {
    private static Logger logger = LoggerFactory.getLogger(ClueScoreRuleController.class);
    @Autowired
    private ClueAssignRuleFeignClient clueAssignRuleFeignClient;
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
        return "assignrule/updateCluesScor";
    }

}
