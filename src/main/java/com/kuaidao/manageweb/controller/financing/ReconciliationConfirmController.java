/**
 * 
 */
package com.kuaidao.manageweb.controller.financing;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.kuaidao.businessconfig.constant.AggregationConstant;
import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.businessconfig.dto.project.ProjectInfoPageParam;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
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
import com.kuaidao.aggregation.dto.financing.ReconciliationConfirmDTO;
import com.kuaidao.aggregation.dto.financing.ReconciliationConfirmPageParam;
import com.kuaidao.aggregation.dto.financing.ReconciliationConfirmReq;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.area.SysRegionFeignClient;
import com.kuaidao.manageweb.feign.customfield.CustomFieldFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.financing.ReconciliationConfirmFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.user.SysSettingFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.area.SysRegionDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldQueryDTO;
import com.kuaidao.sys.dto.customfield.QueryFieldByRoleAndMenuReq;
import com.kuaidao.sys.dto.customfield.QueryFieldByUserAndMenuReq;
import com.kuaidao.sys.dto.customfield.UserFieldDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.SysSettingDTO;
import com.kuaidao.sys.dto.user.SysSettingReq;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

/**
 * @author zxy 对账结算确认
 */

@Controller
@RequestMapping("/financing/reconciliationConfirm")
public class ReconciliationConfirmController {
    private static Logger logger = LoggerFactory.getLogger(ReconciliationConfirmController.class);
    @Autowired
    private ReconciliationConfirmFeignClient reconciliationConfirmFeignClient;
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

    @Autowired
    private CustomFieldFeignClient customFieldFeignClient;
    @Autowired
    private SysSettingFeignClient sysSettingFeignClient;

    /***
     * 对账结算确认页
     * 
     * @return
     */
    @RequestMapping("/initManager")
    @RequiresPermissions("financing:reconciliationConfirmManager:view")
    public String initReconciliationConfirmManager(HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 查询所有商务大区
        List<OrganizationRespDTO> busAreaList =
                getOrgList(null, OrgTypeConstant.SWDQ, user.getBusinessLine());
        request.setAttribute("busAreaList", busAreaList);
        // 查询所有电销事业部
        List<OrganizationRespDTO> teleDeptList =
                getOrgList(null, OrgTypeConstant.DZSYB, user.getBusinessLine());
        request.setAttribute("teleDeptList", teleDeptList);
        // 查询所有项目
//        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();
//        request.setAttribute("projectList", allProject.getData());
        // 查询所有签约项目
        ProjectInfoPageParam param=new ProjectInfoPageParam();
        param.setIsNotSign(AggregationConstant.NO);
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.queryBySign(param);
        request.setAttribute("projectList", allProject.getData());
        // 查询所有省
        JSONResult<List<SysRegionDTO>> getproviceList = sysRegionFeignClient.getproviceList();
        request.setAttribute("provinceList", getproviceList.getData());
        // 查询签约店型集合
        request.setAttribute("vistitStoreTypeList",
                getDictionaryByCode(DicCodeEnum.VISITSTORETYPE.getCode()));
        // 根据角色查询页面字段
        QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq = new QueryFieldByRoleAndMenuReq();
        queryFieldByRoleAndMenuReq.setMenuCode("financing:reconciliationConfirmManager");
        queryFieldByRoleAndMenuReq.setId(user.getRoleList().get(0).getId());
        JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu =
                customFieldFeignClient.queryFieldByRoleAndMenu(queryFieldByRoleAndMenuReq);
        request.setAttribute("fieldList", queryFieldByRoleAndMenu.getData());

        // 根据用户查询页面字段
        QueryFieldByUserAndMenuReq queryFieldByUserAndMenuReq = new QueryFieldByUserAndMenuReq();
        queryFieldByUserAndMenuReq.setId(user.getId());
        queryFieldByUserAndMenuReq.setRoleId(user.getRoleList().get(0).getId());
        queryFieldByUserAndMenuReq.setMenuCode("financing:reconciliationConfirmManager");
        JSONResult<List<UserFieldDTO>> queryFieldByUserAndMenu =
                customFieldFeignClient.queryFieldByUserAndMenu(queryFieldByUserAndMenuReq);
        request.setAttribute("userFieldList", queryFieldByUserAndMenu.getData());
        request.setAttribute("payModeItem", getDictionaryByCode(DicCodeEnum.PAYMODE.getCode()));
        request.setAttribute("giveTypeList", getDictionaryByCode(Constants.GIVE_TYPE));
        return "financing/reconciliationConfirmPage";
    }

    /***
     * 对账结算确认列表
     * 
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    @RequiresPermissions("financing:reconciliationConfirmManager:view")
    public JSONResult<PageBean<ReconciliationConfirmDTO>> list(
            @RequestBody ReconciliationConfirmPageParam pageParam, HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        pageParam.setUserId(user.getId());
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {
            pageParam.setRoleCode(roleList.get(0).getRoleCode());
        }
        pageParam.setBusinessLine(user.getBusinessLine());
        String notInCompanyIds = getSysSetting(SysConstant.NOT_IN_CONFIRM_COMPANY_IDS);
        if (notInCompanyIds != null && StringUtils.isNotBlank(notInCompanyIds)) {
            pageParam.setNotInCompanyIds(notInCompanyIds);
        }
        JSONResult<PageBean<ReconciliationConfirmDTO>> list =
                reconciliationConfirmFeignClient.list(pageParam);
        return list;
    }

    /**
     * 导出
     * 
     * @param reqDTO
     * @return
     */
    @RequiresPermissions("financing:reconciliationConfirmManager:export")
    @PostMapping("/export")
    @LogRecord(description = "导出", operationType = OperationType.EXPORT,
            menuName = MenuEnum.RECONCILIATIONCONFIRM_MANAGER)
    public void export(@RequestBody ReconciliationConfirmPageParam pageParam, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        logger.debug("list param{}", pageParam);
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        pageParam.setUserId(user.getId());
        List<RoleInfoDTO> roleList = user.getRoleList();
        String roleCode = "";
        if (roleList != null) {
            roleCode = roleList.get(0).getRoleCode();
            pageParam.setRoleCode(roleList.get(0).getRoleCode());
        }
        pageParam.setBusinessLine(user.getBusinessLine());
        String notInCompanyIds = getSysSetting(SysConstant.NOT_IN_CONFIRM_COMPANY_IDS);
        if (notInCompanyIds != null && StringUtils.isNotBlank(notInCompanyIds)) {
            pageParam.setNotInCompanyIds(notInCompanyIds);
        }

        JSONResult<List<ReconciliationConfirmDTO>> listNoPage = reconciliationConfirmFeignClient.listNoPage(pageParam);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getHeadTitleList(roleCode));

        if (JSONResult.SUCCESS.equals(listNoPage.getCode()) && listNoPage.getData() != null && listNoPage.getData().size() != 0) {

            List<ReconciliationConfirmDTO> resultList = listNoPage.getData();
            int size = resultList.size();

            for (int i = 0; i < size; i++) {
                ReconciliationConfirmDTO dto = resultList.get(i);
                List<Object> curList = new ArrayList<>();
                curList.add(i + 1);
                if (RoleCodeEnum.QDSJCW.name().equals(roleCode)) {
                    // 渠道速建财务（小物种业务线）导出表格为：
                    // 付款日期，客户姓名，结算单位，电销组，签约项目，付款类型，签约店型，业绩金额，结算金额，
                    // 实收金额，结算比例，优惠金额，赠送金额，佣金，路费，赠送类型，备注，商务经理，签约区域，是否已结算
                    curList.add(getTimeStr(dto.getPayTime()));
                    curList.add(dto.getCusName());
                    curList.add(dto.getSignCompanyName());
                    curList.add(dto.getTeleGorupName());
                    curList.add(dto.getProjectName());
                    curList.add(dto.getPayTypeName());
                    curList.add(dto.getSignShopTypeName());
                    curList.add(dto.getAmountPerformance());
                    curList.add(dto.getMoney());
                    curList.add(dto.getAmountReceived());
                    curList.add(dto.getRatio() + "%");
                    curList.add(dto.getPreferentialAmount());
                    curList.add(dto.getGiveAmount());
                    curList.add(dto.getCommissionMoney());
                    curList.add(dto.getFirstToll());
                    curList.add(dto.getGiveTypeName());
                    curList.add(dto.getRemarks());
                    curList.add(dto.getBusSaleName());
                    curList.add(dto.getSignProvince() + dto.getSignCity() + dto.getSignDictrict());
                    curList.add(dto.getIsAccount());
                    dataList.add(curList);
                } else if (RoleCodeEnum.SJHZCW.name().equals(roleCode)) {
                    // (商机盒子财务)商务盒子业务线导出表格为：
                    // 付款日期，客户姓名，签约项目，签约店型，签约区域，支付方式，付款类型，结算单位，电销组，签约项目，付款类型，
                    // 结算金额，实收金额，路费，优惠金额，赠送金额，赠送类型，结算比例，佣金，是否已结算，商务经理，电销组，电销顾问，电销总监，备注
                    curList.add(getTimeStr(dto.getPayTime()));
                    curList.add(dto.getCusName());
                    curList.add(dto.getProjectName());
                    curList.add(dto.getSignShopTypeName());
                    curList.add(dto.getSignProvince() + dto.getSignCity() + dto.getSignDictrict());
                    curList.add(dto.getPayModeName());
                    curList.add(dto.getPayTypeName());
                    curList.add(dto.getSignCompanyName());
                    curList.add(dto.getTeleGorupName());
                    curList.add(dto.getMoney());
                    curList.add(dto.getAmountReceived());
                    curList.add(dto.getFirstToll());
                    curList.add(dto.getPreferentialAmount());
                    curList.add(dto.getGiveAmount());
                    curList.add(dto.getGiveTypeName());
                    curList.add(dto.getRatio() + "%");
                    curList.add(dto.getCommissionMoney());
                    curList.add(dto.getIsAccount());
                    curList.add(dto.getBusSaleName());
                    curList.add(dto.getTeleSaleName());
                    curList.add(dto.getTeleDirectorName());
                    curList.add(dto.getRemarks());
                    dataList.add(curList);
                }
            }

        } else {
            logger.error("export rule_report res{{}}", listNoPage);
        }
        XSSFWorkbook workBook = new XSSFWorkbook();// 创建一个工作薄
        XSSFSheet sheet = workBook.createSheet();// 创建一个工作薄对象sheet
        // 设置宽度
        if (RoleCodeEnum.QDSJCW.name().equals(roleCode)) {
            sheet.setColumnWidth(1, 4000);
            sheet.setColumnWidth(3, 4000);
            sheet.setColumnWidth(5, 6000);
            sheet.setColumnWidth(17, 6000);
            sheet.setColumnWidth(19, 6000);
        } else if (RoleCodeEnum.SJHZCW.name().equals(roleCode)) {

            sheet.setColumnWidth(1, 4000);
            sheet.setColumnWidth(3, 5000);
            sheet.setColumnWidth(5, 6000);
            sheet.setColumnWidth(22, 8000);
        }
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007ExcelWorkbook(workBook, dataList);

        String name = "对账结算确认" + DateUtil.convert2String(new Date(), DateUtil.ymdhms2) + ".xlsx";
        response.addHeader("Content-Disposition", "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();

    }

    private List<Object> getHeadTitleList(String roleCode) {

        List<Object> headTitleList = new ArrayList<>();
        if (RoleCodeEnum.QDSJCW.name().equals(roleCode)) {
            // 渠道速建财务（小物种业务线）导出表格为：
            // 付款日期，客户姓名，结算单位，电销组，签约项目，付款类型，签约店型，业绩金额，结算金额，
            // 实收金额，结算比例，优惠金额，赠送金额，佣金，路费，赠送类型，备注，商务经理，签约区域，是否已结算

            headTitleList.add("序号");
            headTitleList.add("付款日期");
            headTitleList.add("客户姓名");
            headTitleList.add("结算单位");
            headTitleList.add("电销组");
            headTitleList.add("签约项目");
            headTitleList.add("付款类型");
            headTitleList.add("签约店型");
            headTitleList.add("业绩金额");
            headTitleList.add("结算金额");
            headTitleList.add("实收金额");
            headTitleList.add("结算比例");
            headTitleList.add("优惠金额");
            headTitleList.add("赠送金额");
            headTitleList.add("佣金");
            headTitleList.add("路费");
            headTitleList.add("赠送类型");
            headTitleList.add("备注");
            headTitleList.add("商务经理");
            headTitleList.add("签约区域");
            headTitleList.add("是否已结算");
        } else if (RoleCodeEnum.SJHZCW.name().equals(roleCode)) {
            // (商机盒子财务)商务盒子业务线导出表格为：
            // 付款日期，客户姓名，签约项目，签约店型，签约区域，支付方式，付款类型，结算单位，电销组，签约项目，付款类型，
            // 结算金额，实收金额，路费，优惠金额，赠送金额，赠送类型，结算比例，佣金，是否已结算，商务经理，电销组，电销顾问，电销总监，备注
            headTitleList.add("序号");
            headTitleList.add("付款日期");
            headTitleList.add("客户姓名");
            headTitleList.add("签约项目");
            headTitleList.add("签约店型");
            headTitleList.add("签约区域");
            headTitleList.add("支付方式");
            headTitleList.add("付款类型");
            headTitleList.add("结算单位");
            headTitleList.add("电销组");
            headTitleList.add("结算金额");
            headTitleList.add("实收金额");
            headTitleList.add("路费");
            headTitleList.add("优惠金额");
            headTitleList.add("赠送金额");
            headTitleList.add("赠送类型");
            headTitleList.add("结算比例");
            headTitleList.add("佣金");
            headTitleList.add("是否已结算");
            headTitleList.add("商务经理");
            headTitleList.add("电销顾问");
            headTitleList.add("电销总监");
            headTitleList.add("备注");
        }

        return headTitleList;
    }

    /***
     * 对账确认
     * 
     * @return
     */
    @PostMapping("/reconciliationConfirm")
    @ResponseBody
    @RequiresPermissions("financing:reconciliationConfirmManager:reconciliation")
    @LogRecord(description = "对账确认", operationType = OperationType.UPDATE,
            menuName = MenuEnum.RECONCILIATIONCONFIRM_MANAGER)
    public JSONResult<Void> applyRefund(@RequestBody ReconciliationConfirmReq req,
            HttpServletRequest request) {
    	UserInfoDTO user = getUser();
        req.setStatus(AggregationConstant.RECONCILIATION_STATUS.STATUS_3);
        req.setCreateUser(user.getId());
        JSONResult<Void> reconciliationConfirm =
                reconciliationConfirmFeignClient.reconciliationConfirm(req);
        return reconciliationConfirm;
    }

    /***
     * 结算确认
     * 
     * @return
     */
    @PostMapping("/settlementConfirm")
    @ResponseBody
    @RequiresPermissions("financing:reconciliationConfirmManager:settlement")
    @LogRecord(description = "结算确认", operationType = OperationType.UPDATE,
            menuName = MenuEnum.RECONCILIATIONCONFIRM_MANAGER)
    public JSONResult<Void> settlementConfirm(@RequestBody ReconciliationConfirmReq req,
            HttpServletRequest request) {
        req.setStatus(AggregationConstant.RECONCILIATION_STATUS.STATUS_4);
        JSONResult<Void> reconciliationConfirm =
                reconciliationConfirmFeignClient.reconciliationConfirm(req);
        return reconciliationConfirm;
    }

    /***
     * 餐饮公司返款申请
     * 
     * @return
     */
    @PostMapping("/sumCommissionMoney")
    @ResponseBody
    @RequiresPermissions("financing:reconciliationConfirmManager:view")
    public JSONResult<BigDecimal> sumCommissionMoney(
            @RequestBody ReconciliationConfirmPageParam pageParam, HttpServletRequest request) {
        UserInfoDTO user = getUser();
        pageParam.setUserId(user.getId());
        pageParam.setBusinessLine(user.getBusinessLine());
        JSONResult<BigDecimal> sumCommissionMoney =
                reconciliationConfirmFeignClient.sumCommissionMoney(pageParam);
        return sumCommissionMoney;
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
     * 获取所有组织组
     * 
     * @param orgDTO
     * @return
     */
    private List<OrganizationRespDTO> getOrgList(Long parentId, Integer type,
            Integer businessLine) {
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        queryDTO.setParentId(parentId);
        queryDTO.setOrgType(type);
        queryDTO.setBusinessLine(businessLine);
        // 查询所有组织
        JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                organizationFeignClient.queryOrgByParam(queryDTO);
        List<OrganizationRespDTO> data = queryOrgByParam.getData();
        return data;
    }

    /**
     * 获取所有商务经理（组织名-大区名）
     * 
     * @param orgDTO
     * @return
     */
    private List<Map<String, Object>> getAllSaleList() {
        // 查询所有商务组
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        queryDTO.setOrgType(OrgTypeConstant.SWZ);
        JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                organizationFeignClient.queryOrgByParam(queryDTO);
        List<OrganizationRespDTO> groupList = queryOrgByParam.getData();
        // 查询所有商务大区
        queryDTO.setOrgType(OrgTypeConstant.SWDQ);
        JSONResult<List<OrganizationRespDTO>> busArea =
                organizationFeignClient.queryOrgByParam(queryDTO);
        List<OrganizationRespDTO> busAreaLsit = busArea.getData();
        // 查询所有商务经理
        List<Integer> statusList = new ArrayList<Integer>();
        statusList.add(SysConstant.USER_STATUS_ENABLE);
        statusList.add(SysConstant.USER_STATUS_LOCK);
        List<UserInfoDTO> userList = getUserList(null, RoleCodeEnum.SWJL.name(), statusList);

        Map<Long, OrganizationRespDTO> orgMap = new HashMap<Long, OrganizationRespDTO>();
        // 生成<机构id，机构>map
        for (OrganizationRespDTO org : groupList) {
            orgMap.put(org.getId(), org);
        }
        for (OrganizationRespDTO org : busAreaLsit) {
            orgMap.put(org.getId(), org);
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        // 生成结果集，匹配电销组以及电销总监
        for (UserInfoDTO user : userList) {
            Map<String, Object> resultMap = new HashMap<String, Object>();
            OrganizationRespDTO group = orgMap.get(user.getOrgId());
            if (group != null) {
                OrganizationRespDTO area = orgMap.get(group.getParentId());
                resultMap.put("id", user.getId().toString());
                if (area != null) {
                    resultMap.put("name",
                            user.getName() + "(" + area.getName() + "--" + group.getName() + ")");
                } else {
                    resultMap.put("name", user.getName() + "(" + group.getName() + ")");

                }
                result.add(resultMap);
            }
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

    private String getTimeStr(Date date) {
        if (date == null) {
            return "";
        }
        return DateUtil.convert2String(date, DateUtil.ymd);
    }
}
