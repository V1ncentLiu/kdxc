/**
 * 
 */
package com.kuaidao.manageweb.controller.rule;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.businessconfig.dto.rule.ClueAssignRuleDTO;
import com.kuaidao.businessconfig.dto.rule.ClueAssignRulePageParam;
import com.kuaidao.businessconfig.dto.rule.RuleReportDTO;
import com.kuaidao.businessconfig.dto.rule.RuleReportPageParam;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.rule.ClueAssignRuleFeignClient;
import com.kuaidao.manageweb.feign.rule.RuleReportFeignClient;
import com.kuaidao.manageweb.feign.user.SysSettingFeignClient;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
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
@RequestMapping("/clueAssignRule/ruleReport")
public class RuleReportController {
    private static Logger logger = LoggerFactory.getLogger(RuleReportController.class);
    @Autowired
    private RuleReportFeignClient ruleReportFeignClient;
    @Autowired
    private ClueAssignRuleFeignClient clueAssignRuleFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;
    @Autowired
    private SysSettingFeignClient sysSettingFeignClient;

    /***
     * 规则报表列表页
     * 
     * @return
     */
    @RequestMapping("/initList")
    @RequiresPermissions("clueAssignRule:ruleReport:view")
    public String initCompanyList(HttpServletRequest request) {
        // 查询类资源类别集合
        request.setAttribute("categoryList", getDictionaryByCode(Constants.CLUE_CATEGORY));
        // 查询类资源类型集合
        request.setAttribute("typeList", getDictionaryByCode(Constants.CLUE_TYPE));
        // 查询字典媒介集合
        request.setAttribute("sourceList", getDictionaryByCode(Constants.MEDIUM));
        // 查询字典广告位集合
        request.setAttribute("sourceTypeList", getDictionaryByCode(Constants.ADSENSE));
        // 查询字典行业类别集合
        request.setAttribute("industryCategoryList",
                getDictionaryByCode(Constants.INDUSTRY_CATEGORY));
        // 所有规则
        ClueAssignRulePageParam pageParam = new ClueAssignRulePageParam();
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        pageParam.setUserId(user.getId());
        pageParam.setOrgId(user.getOrgId());

        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {

            pageParam.setRoleCode(roleList.get(0).getRoleCode());
        }
        JSONResult<List<ClueAssignRuleDTO>> allValidRule =
                clueAssignRuleFeignClient.allValidRule(pageParam);
        request.setAttribute("ruleList", allValidRule.getData());

        OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
        orgDto.setOrgType(OrgTypeConstant.DXZ);
        orgDto.setSystemCode(SystemCodeConstant.HUI_JU);
        // 电销小组
        JSONResult<List<OrganizationRespDTO>> dzList =
                organizationFeignClient.queryOrgByParam(orgDto);
        List<OrganizationRespDTO> data = dzList.getData();
        request.setAttribute("queryOrg", data);
        return "rule/ruleReportManagerPage";
    }


    /***
     * 规则报表列表
     * 
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    @RequiresPermissions("clueAssignRule:ruleReport:view")
    public JSONResult<PageBean<RuleReportDTO>> list(@RequestBody RuleReportPageParam pageParam,
            HttpServletRequest request) {
        logger.debug("list param{}", pageParam);
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        pageParam.setUserId(user.getId());
        pageParam.setOrgId(user.getOrgId());

        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {

            pageParam.setRoleCode(roleList.get(0).getRoleCode());
        }
        // 规则报表
        JSONResult<PageBean<RuleReportDTO>> list = ruleReportFeignClient.list(pageParam);

        return list;
    }

    /**
     * 导出
     * 
     * @param reqDTO
     * @return
     */
    @RequiresPermissions("clueAssignRule:ruleReport:export")
    @PostMapping("/export")
    @LogRecord(description = "规则报表导出", operationType = OperationType.EXPORT,
            menuName = MenuEnum.CLUE_RULE_REPORT)
    public void export(@RequestBody RuleReportPageParam pageParam, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("list param{}", pageParam);
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        pageParam.setUserId(user.getId());
        pageParam.setOrgId(user.getOrgId());

        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {

            pageParam.setRoleCode(roleList.get(0).getRoleCode());
        }



        JSONResult<List<RuleReportDTO>> listNoPage = ruleReportFeignClient.listNoPage(pageParam);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getHeadTitleList());

        if (JSONResult.SUCCESS.equals(listNoPage.getCode()) && listNoPage.getData() != null
                && listNoPage.getData().size() != 0) {

            List<RuleReportDTO> resultList = listNoPage.getData();
            int size = resultList.size();

            for (int i = 0; i < size; i++) {
                RuleReportDTO ruleReportDTO = resultList.get(i);
                List<Object> curList = new ArrayList<>();
                curList.add(i + 1);
                curList.add(ruleReportDTO.getRuleName());
                curList.add(ruleReportDTO.getTeleOrgName());
                curList.add(ruleReportDTO.getSourceName());
                curList.add(ruleReportDTO.getTypeName());
                curList.add(ruleReportDTO.getCategoryName());
                curList.add(ruleReportDTO.getSourceTypeName());
                curList.add(ruleReportDTO.getSearchWord());
                curList.add(ruleReportDTO.getNotSearchWord());
                curList.add(ruleReportDTO.getRuleTypeName());
                curList.add(ruleReportDTO.getResourceMaxNumName());
                curList.add(ruleReportDTO.getProportion());
                curList.add(ruleReportDTO.getAssignNumber());
                dataList.add(curList);
            }

        } else {
            logger.error("export rule_report res{{}}", listNoPage);
        }

        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);


        String name = "规则报表" + DateUtil.convert2String(new Date(), DateUtil.ymd) + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();

    }

    private List<Object> getHeadTitleList() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("规则名称");
        headTitleList.add("电销组");
        headTitleList.add("媒介");
        headTitleList.add("资源类型");
        headTitleList.add("资源类别");
        headTitleList.add("广告位");
        headTitleList.add("包含搜索词");
        headTitleList.add("不包含搜索词");
        headTitleList.add("规则属性");
        headTitleList.add("资源上限");
        headTitleList.add("分配比例");
        headTitleList.add("已分配条数");
        return headTitleList;
    }

    private String getTimeStr(Date date) {
        if (date == null) {
            return "";
        }
        return DateUtil.convert2String(date, DateUtil.ymdhms);
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

}
