/**
 *
 */
package com.kuaidao.manageweb.controller.rule;

import com.kuaidao.businessconfig.constant.AggregationConstant;
import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.businessconfig.dto.project.ProjectInfoPageParam;
import com.kuaidao.businessconfig.dto.rule.AssignRuleTeamDTO;
import com.kuaidao.businessconfig.dto.rule.ClueAssignRuleDTO;
import com.kuaidao.businessconfig.dto.rule.ClueAssignRulePageParam;
import com.kuaidao.businessconfig.dto.rule.ClueAssignRuleReq;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.common.util.ExcelUtil;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
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
    @RequestMapping("/telemarketingScore")
    public String initCompanyList(HttpServletRequest request) {
        return "assignrule/telemarketingScore";
    }

    /***
     * 优化规则列表页
     *
     * @return
     */
    @RequestMapping("/addtelemarketingScore")
    public String addtelemarketingScore(HttpServletRequest request) {
        return "assignrule/addtelemarketingScore";
    }
    /***
     * 优化规则列表页
     *
     * @return
     */
    @RequestMapping("/updatetelemarketingScore")
    public String updatetelemarketingScore(HttpServletRequest request) {
        return "assignrule/updatetelemarketingScore";
    }

}
