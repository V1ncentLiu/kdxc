package com.kuaidao.manageweb.controller.financing;

import com.kuaidao.aggregation.dto.financing.ReconciliationConfirmDTO;
import com.kuaidao.aggregation.dto.financing.ReconciliationConfirmPageParam;
import com.kuaidao.aggregation.dto.financing.ReconciliationConfirmReq;
import com.kuaidao.aggregation.dto.paydetail.PayDetailAccountDTO;
import com.kuaidao.businessconfig.constant.AggregationConstant;
import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.businessconfig.dto.project.ProjectInfoPageParam;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
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
import com.kuaidao.manageweb.feign.financing.BalanceAccountApplyClient;
import com.kuaidao.manageweb.feign.financing.ReconciliationConfirmFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.area.SysRegionDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldQueryDTO;
import com.kuaidao.sys.dto.customfield.QueryFieldByRoleAndMenuReq;
import com.kuaidao.sys.dto.customfield.QueryFieldByUserAndMenuReq;
import com.kuaidao.sys.dto.customfield.UserFieldDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 退返款
 * 
 * @author Chen
 * @date 2019年4月10日 下午7:23:08
 * @version V1.0
 */
@RequestMapping("/financing/balanceaccount")
@Controller
public class BalanceAccountController {
    private static Logger logger = LoggerFactory.getLogger(BalanceAccountController.class);
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
    private BalanceAccountApplyClient balanceAccountApplyClient;
    @Autowired
    private CustomFieldFeignClient customFieldFeignClient;
    private Configuration configuration = null;

    public BalanceAccountController() {
        configuration = new Configuration();
        configuration.setDefaultEncoding("utf-8");
    }

    /**
     * 申请页面
     * 
     * @return
     */
    @RequestMapping("/balanceAccountPage")
    @RequiresPermissions("financing:balanceaccountManager:view")
    public String balanceAccountPage(HttpServletRequest request) {
        UserInfoDTO user = getUser();
        Long busAreaStartdate = System.currentTimeMillis();
        // 查询所有商务大区
        List<OrganizationRespDTO> busAreaList =
                getOrgList(null, OrgTypeConstant.SWDQ, user.getBusinessLine());
        request.setAttribute("busAreaList", busAreaList);
        Long busendStartdate = System.currentTimeMillis();
        logger.info("所有商务大区查询时间" + (busendStartdate - busAreaStartdate));
        // // 查询所有商务组
        // List<OrganizationRespDTO> busGroupList =
        // getOrgList(null, OrgTypeConstant.SWZ, user.getBusinessLine());
        // request.setAttribute("busGroupList", busGroupList);
        Long busGrouptdate = System.currentTimeMillis();
        // logger.info("所有商务组查询时间"+(busGrouptdate-busendStartdate));
        // 查询所有电销事业部
        List<OrganizationRespDTO> teleDeptList =
                getOrgList(null, OrgTypeConstant.DZSYB, user.getBusinessLine());
        request.setAttribute("teleDeptList", teleDeptList);
        Long teleDeptdate = System.currentTimeMillis();
        logger.info("所有电销事业部查询时间" + (teleDeptdate - busGrouptdate));
        // 查询所有电销组
        // List<OrganizationRespDTO> teleGroupList =
        // getOrgList(null, OrgTypeConstant.DXZ, user.getBusinessLine());
        // request.setAttribute("teleGroupList", teleGroupList);
        Long teleGroupdate = System.currentTimeMillis();
        logger.info("所有电销组查询时间" + (teleGroupdate - teleDeptdate));
        // 查询所有商务经理
        // List<UserInfoDTO> busSaleList =
        // getUserList(null, RoleCodeEnum.SWJL.name(), null, user.getBusinessLine());
        // request.setAttribute("busSaleList", busSaleList);
        Long busSaledate = System.currentTimeMillis();
        logger.info("所有商务经理查询时间" + (busSaledate - teleGroupdate));
        // 查询所有电销创业顾问
        // List<UserInfoDTO> teleSaleList =
        // getUserList(null, RoleCodeEnum.DXCYGW.name(), null, user.getBusinessLine());
        // request.setAttribute("teleSaleList", teleSaleList);
        Long teleSaledate = System.currentTimeMillis();
        logger.info("所有电销创业顾问查询时间" + (teleSaledate - busSaledate));

        // 查询所有签约项目
        ProjectInfoPageParam param=new ProjectInfoPageParam();
        param.setIsNotSign(AggregationConstant.NO);
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.queryBySign(param);
        request.setAttribute("projectList", allProject.getData());
        // 查询所有项目
//        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();
//        request.setAttribute("projectList", allProject.getData());
        Long allProjectdate = System.currentTimeMillis();
        logger.info("查询所有项目查询时间" + (allProjectdate - teleSaledate));
        // 查询所有省
        JSONResult<List<SysRegionDTO>> getproviceList = sysRegionFeignClient.getproviceList();
        request.setAttribute("provinceList", getproviceList.getData());
        Long getprovicetdate = System.currentTimeMillis();
        logger.info("查询所有省查询时间" + (getprovicetdate - allProjectdate));
        // 根据角色查询页面字段
        QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq = new QueryFieldByRoleAndMenuReq();
        queryFieldByRoleAndMenuReq.setMenuCode("financing:balanceaccountManager");
        queryFieldByRoleAndMenuReq.setId(user.getRoleList().get(0).getId());
        JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu =
                customFieldFeignClient.queryFieldByRoleAndMenu(queryFieldByRoleAndMenuReq);
        request.setAttribute("fieldList", queryFieldByRoleAndMenu.getData());
        Long queryFieldByRoleAndMenuReqdate = System.currentTimeMillis();
        logger.info("角色查询页面字段查询时间" + (queryFieldByRoleAndMenuReqdate - getprovicetdate));
        // 根据用户查询页面字段
        QueryFieldByUserAndMenuReq queryFieldByUserAndMenuReq = new QueryFieldByUserAndMenuReq();
        queryFieldByUserAndMenuReq.setId(user.getId());
        queryFieldByUserAndMenuReq.setRoleId(user.getRoleList().get(0).getId());
        queryFieldByUserAndMenuReq.setMenuCode("financing:balanceaccountManager");
        JSONResult<List<UserFieldDTO>> queryFieldByUserAndMenu =
                customFieldFeignClient.queryFieldByUserAndMenu(queryFieldByUserAndMenuReq);
        request.setAttribute("userFieldList", queryFieldByUserAndMenu.getData());
        Long queryFieldByUserAndMenuReqdate = System.currentTimeMillis();
        logger.info(
                "用户查询页面字段查询时间" + (queryFieldByUserAndMenuReqdate - queryFieldByRoleAndMenuReqdate));
        // 查询签约店型集合
        request.setAttribute("vistitStoreTypeList",
                getDictionaryByCode(DicCodeEnum.VISITSTORETYPE.getCode()));
        request.setAttribute("payModeItem", getDictionaryByCode(DicCodeEnum.PAYMODE.getCode()));
        logger.info("财务对账申请总共时间" + (queryFieldByUserAndMenuReqdate - busAreaStartdate));
        request.setAttribute("businessLine", user.getBusinessLine());
        return "financing/balanceAccountPage";
    }
    
    /***
     * 对账结算申请列表
     * @return
     */
    @PostMapping("/applyList")
    @ResponseBody
    @RequiresPermissions("financing:balanceaccountManager:view")
    public JSONResult<PageBean<ReconciliationConfirmDTO>> appayList(@RequestBody ReconciliationConfirmPageParam pageParam,
            HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        pageParam.setUserId(user.getId());
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {

            pageParam.setRoleCode(roleList.get(0).getRoleCode());
        }
        pageParam.setBusinessLine(user.getBusinessLine());
        Long start = System.currentTimeMillis();
        JSONResult<PageBean<ReconciliationConfirmDTO>> list = reconciliationConfirmFeignClient.applyList(pageParam);
        logger.info("财务对账列表总共时间:{} ", System.currentTimeMillis() - start);
        return list;
    }

    /**
     * 导出
     * @param
     * @return
     */
    @RequiresPermissions("financing:balanceaccountManager:export")
    @PostMapping("/export")
    @LogRecord(description = "导出", operationType = OperationType.EXPORT, menuName = MenuEnum.REFUNDREBATEAPPLY_MANAGER)
    public void export(@RequestBody ReconciliationConfirmPageParam pageParam, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        logger.debug("list param{}", pageParam);
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        pageParam.setUserId(user.getId());

        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {
            pageParam.setRoleCode(roleList.get(0).getRoleCode());
        }
        pageParam.setBusinessLine(user.getBusinessLine());

        JSONResult<List<ReconciliationConfirmDTO>> listNoPage = reconciliationConfirmFeignClient.applyListNoPage(pageParam);
        List<List<Object>> dataList = new ArrayList<>();
        dataList.add(getHeadTitleList());

        if (JSONResult.SUCCESS.equals(listNoPage.getCode()) && listNoPage.getData() != null && listNoPage.getData().size() != 0) {

            List<ReconciliationConfirmDTO> resultList = listNoPage.getData();
            int size = resultList.size();

            for (int i = 0; i < size; i++) {
                ReconciliationConfirmDTO dto = resultList.get(i);
                List<Object> curList = new ArrayList<>();
                curList.add(i + 1);
                // 付款日期，客户姓名，签约项目，签约店型，签约区域，联系方式，身份证号码，电销组，商务经理，支付方式，实收金额，款项来源，
                // 付款类型，应收金额，业绩金额，结算金额，结算比例，佣金，路费，优惠金额，赠送金额，备注,款项来源默认显示都为空。
                curList.add(getTimeStr(dto.getPayTime()));
                curList.add(dto.getCusName());
                curList.add(dto.getProjectName());
                curList.add(dto.getSignShopTypeName());
                curList.add(dto.getSignProvince() + dto.getSignCity() + dto.getSignDictrict());
                curList.add(dto.getPhone());
                curList.add(dto.getIdCard());
                curList.add(dto.getTeleGorupName());
                curList.add(dto.getTeleSaleName());
                curList.add(dto.getBusSaleName());
                curList.add(dto.getPayModeName());
                curList.add(dto.getAmountReceived());
                curList.add(dto.getAmountEquipment());
                curList.add("");
                curList.add(dto.getPayTypeName());
                curList.add(dto.getAmountReceivable());
                curList.add(dto.getTeleAmountPerformance());
                curList.add(dto.getAmountPerformance());
                curList.add(dto.getMoney());
                if (org.apache.commons.lang.StringUtils.isNotBlank(dto.getRatio())) {
                    curList.add(dto.getRatio() + "%");
                }else{
                    curList.add("");
                }
                curList.add(dto.getCommissionMoney());
                curList.add(dto.getFirstToll());
                curList.add(dto.getPreferentialAmount());
                curList.add(dto.getGiveAmount());
                curList.add(dto.getRemarks());
                dataList.add(curList);
            }

        } else {
            logger.error("export rule_report res{{}}", listNoPage);
        }

        XSSFWorkbook workBook = new XSSFWorkbook();// 创建一个工作薄
        XSSFSheet sheet = workBook.createSheet();// 创建一个工作薄对象sheet
        // 设置宽度
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(3, 5000);
        sheet.setColumnWidth(5, 6000);
        sheet.setColumnWidth(6, 4000);
        sheet.setColumnWidth(7, 4000);
        sheet.setColumnWidth(22, 8000);
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007ExcelWorkbook(workBook, dataList);

        String name = "对账结算申请" + DateUtil.convert2String(new Date(), DateUtil.ymdhms2) + ".xlsx";
        response.addHeader("Content-Disposition", "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();

    }

    private List<Object> getHeadTitleList() {
        // 付款日期，客户姓名，签约项目，签约店型，签约区域，联系方式，身份证号码，电销组，商务经理，支付方式，实收金额，款项来源，
        // 付款类型，应收金额，业绩金额，结算金额，结算比例，佣金，路费，优惠金额，赠送金额，备注,款项来源默认显示都为空。

        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("付款日期");
        headTitleList.add("客户姓名");
        headTitleList.add("签约项目");
        headTitleList.add("签约店型");
        headTitleList.add("签约区域");
        headTitleList.add("联系方式");
        headTitleList.add("身份证号码");
        headTitleList.add("电销组");
        headTitleList.add("电销顾问");
        headTitleList.add("商务经理");
        headTitleList.add("支付方式");
        headTitleList.add("实收金额");
        headTitleList.add("设备金额");
        headTitleList.add("款项来源");
        headTitleList.add("付款类型");
        headTitleList.add("应收金额");
        headTitleList.add("电销业绩金额");
        headTitleList.add("商务业绩金额");
        headTitleList.add("结算金额");
        headTitleList.add("结算比例");
        headTitleList.add("佣金");
        headTitleList.add("路费");
        headTitleList.add("优惠金额");
        headTitleList.add("赠送金额");
        headTitleList.add("备注");
        return headTitleList;
    }

    /***
     * 驳回
     * 
     * @return
     */
    @PostMapping("/rejectApply")
    @ResponseBody
    @LogRecord(description = "驳回", operationType = OperationType.UPDATE,
            menuName = MenuEnum.REFUNDREBATEAPPLY_MANAGER)
    public JSONResult<Void> rejectApply(@RequestBody ReconciliationConfirmReq req,
            HttpServletRequest request) {
        UserInfoDTO user = CommUtil.getCurLoginUser();
        req.setCommitUser(user.getId());
        req.setStatus(AggregationConstant.RECONCILIATION_STATUS.STATUS_1);
        JSONResult<Void> reconciliationConfirm = reconciliationConfirmFeignClient.rejectApply(req);
        return reconciliationConfirm;
    }

    /***
     * 申请确认
     * 
     * @return
     */
    @PostMapping("/settlementConfirm")
    @ResponseBody
    // @RequiresPermissions("financing:reconciliationConfirmManager:settlementConfirm")
    @LogRecord(description = "结算申请", operationType = OperationType.UPDATE,
            menuName = MenuEnum.RECONCILIATIONCONFIRM_MANAGER)
    public JSONResult<Void> settlementConfirm(@RequestBody ReconciliationConfirmReq req,
            HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        req.setCommitUser(user.getId());
        req.setCommitTime(new Date());
        BigDecimal bigDecimal = new BigDecimal(req.getMoney());
        // req.setCommissionMoney(bigDecimal.multiply(new BigDecimal(req.getRatio())).divide(new
        // BigDecimal(100)));
        req.setStatus(AggregationConstant.RECONCILIATION_STATUS.STATUS_2);
        JSONResult<Void> reconciliationConfirm = reconciliationConfirmFeignClient.applyConfirm(req);
        return reconciliationConfirm;
    }

    /**
     * 根据对账申请表id获取已对账的佣金之和
     * 
     * @author: Fanjd
     * @param reconciliationConfirmReq 请求实体
     * @return: com.kuaidao.common.entity.JSONResult<java.lang.Void>
     * @Date: 2019/6/14 18:25
     * @since: 1.0.0
     **/
    @ResponseBody
    @PostMapping("/getConfirmCommission")
    public JSONResult<BigDecimal> getConfirmCommission(@RequestBody ReconciliationConfirmReq reconciliationConfirmReq) {
        JSONResult<BigDecimal> sumConfirmCommission = reconciliationConfirmFeignClient.getConfirmCommission(reconciliationConfirmReq.getSignId());
        return sumConfirmCommission;
    }

    /**
     * 根据付款明细id获取提交对账确认结算金额初始化
     * @author: Fanjd
     * @param idEntityLong 请求实体
     * @return: com.kuaidao.common.entity.JSONResult<java.lang.Void>
     * @Date: 2020/07/08 18:25
     * @since: 1.0.0
     **/
    @ResponseBody
    @PostMapping("/getSettlementAmount")
    public JSONResult<BigDecimal> getSettlementAmount(@RequestBody IdEntityLong idEntityLong) {
        JSONResult<BigDecimal> settlementAmount = reconciliationConfirmFeignClient.getSettlementAmount(idEntityLong);
        return settlementAmount;
    }
    /**
     * 获取当前登录账号
     * 
     * @return
     */
    private UserInfoDTO getUser() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user;
    }

    /**
     * 根据机构和角色类型获取用户
     * 
     * @return
     */
    private List<UserInfoDTO> getUserList(Long orgId, String roleCode, List<Integer> statusList,
            Integer businessLine) {
        UserOrgRoleReq userOrgRoleReq = new UserOrgRoleReq();
        userOrgRoleReq.setOrgId(orgId);
        userOrgRoleReq.setRoleCode(roleCode);
        userOrgRoleReq.setStatusList(statusList);
        userOrgRoleReq.setBusinessLine(businessLine);
        JSONResult<List<UserInfoDTO>> listByOrgAndRole =
                userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        return listByOrgAndRole.getData();
    }

    /**
     * 获取所有组织组
     * 
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
     * 下载模板
     * 
     * @return
     * @throws Exception
     */
    @RequestMapping("/downBalanceAccount")
    public ModelAndView downBalanceAccount(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        PayDetailAccountDTO queryDTO = new PayDetailAccountDTO();
        queryDTO.setPayDetailId(Long.parseLong(request.getParameter("payDetailId")));
        JSONResult<PayDetailAccountDTO> jsonResult =
                balanceAccountApplyClient.getPayDetailById(queryDTO);
        Map dataMap = new HashMap<>();
        if (JSONResult.SUCCESS.equals(jsonResult.getCode()) && jsonResult.getData() != null) {
            List<DictionaryItemRespDTO> dictionaryItemRespDTOs =
                    getDictionaryByCode(DicCodeEnum.VISITSTORETYPE.getCode());
            List<DictionaryItemRespDTO> giveTypeDTOs = getDictionaryByCode(Constants.GIVE_TYPE);

            List<DictionaryItemRespDTO> payModeItem =
                    getDictionaryByCode(DicCodeEnum.PAYMODE.getCode());


            PayDetailAccountDTO accountDTO = jsonResult.getData();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dataMap.put("signShopType", "");
            dataMap.put("giveTypeName", "");
            // 去字典表查询签约店型
            if (dictionaryItemRespDTOs != null && dictionaryItemRespDTOs.size() > 0) {
                for (DictionaryItemRespDTO dictionaryItemRespDTO : dictionaryItemRespDTOs) {
                    if (dictionaryItemRespDTO.getValue().equals(accountDTO.getSignShopType())) {
                        dataMap.put("signShopType", dictionaryItemRespDTO.getName());
                    }
                }
            }
            // 去字典表查询赠送类型
            if (giveTypeDTOs != null && giveTypeDTOs.size() > 0) {
                for (DictionaryItemRespDTO dictionaryItemRespDTO : giveTypeDTOs) {
                    if (accountDTO.getGiveType() != null && dictionaryItemRespDTO.getValue()
                            .equals(accountDTO.getGiveType().toString())) {
                        dataMap.put("giveTypeName", dictionaryItemRespDTO.getName());
                    }
                }
            }

            // 去字典表查询赠送类型
            if (giveTypeDTOs != null && giveTypeDTOs.size() > 0) {
                for (DictionaryItemRespDTO dictionaryItemRespDTO : giveTypeDTOs) {
                    if (accountDTO.getGiveType() != null && dictionaryItemRespDTO.getValue()
                            .equals(accountDTO.getGiveType().toString())) {
                        dataMap.put("giveTypeName", dictionaryItemRespDTO.getName());
                    }
                }
            }

            String payMode1 = accountDTO.getPayMode();
            String payMode = "";
            if (StringUtils.isNotBlank(payMode1)) {
                String[] split = payMode1.split(",");
                for (int i = 0; i < split.length; i++) {
                    for (DictionaryItemRespDTO item : payModeItem) {
                        if (item.getValue().equals(split[i])) {
                            if (i == 0) {
                                payMode = item.getName();
                            } else {
                                payMode = payMode + "," + item.getName();
                            }
                        }
                    }
                }
            }

            // if (accountDTO.getPayMode() == 1) {
            // payMode = "现金";
            // } else if (accountDTO.getPayMode() == 2) {
            // payMode = "POS";
            // } else if (accountDTO.getPayMode() == 3) {
            // payMode = "转账";
            // } else if (accountDTO.getPayMode() == 4) {
            // payMode = "微信";
            // } else if (accountDTO.getPayMode() == 5) {
            // payMode = "支付宝";
            // }


            String payType = "";
            if (accountDTO.getPayType() == 1) {
                payType = "全款";
            } else if (accountDTO.getPayType() == 2) {
                payType = "定金";
            } else if (accountDTO.getPayType() == 3) {
                payType = "追加定金";
            } else if (accountDTO.getPayType() == 4) {
                payType = "尾款";
            }
            String createTime = sdf.format(accountDTO.getPayTime());
            dataMap.put("year", createTime.substring(0, 4));
            dataMap.put("month", createTime.substring(5, 7));
            dataMap.put("day", createTime.substring(8, 10));
            dataMap.put("statementNo",
                    accountDTO.getStatementNo() == null ? "" : (accountDTO.getStatementNo() + ""));
            dataMap.put("cueName",
                    accountDTO.getCusName() == null ? "" : (accountDTO.getCusName() + ""));
            dataMap.put("phone", accountDTO.getPhone() == null ? "" : (accountDTO.getPhone() + ""));
            dataMap.put("idCard",
                    accountDTO.getIdCard() == null ? "" : (accountDTO.getIdCard() + ""));
            dataMap.put("projectName",
                    accountDTO.getProjectName() == null ? "" : (accountDTO.getProjectName() + ""));
            dataMap.put("area", accountDTO.getSignProvince() + accountDTO.getSignCity()
                    + accountDTO.getSignDictrict());
            dataMap.put("companyName",
                    accountDTO.getCompanyName() == null ? "" : (accountDTO.getCompanyName() + ""));
            dataMap.put("payMode", payMode);
            dataMap.put("payType", payType);
            dataMap.put("received",
                    accountDTO.getAmountReceived() == null ? "" : accountDTO.getAmountReceived());
            dataMap.put("businessManager", accountDTO.getBusinessManagerName() == null ? ""
                    : (accountDTO.getBusinessManagerName() + ""));
            dataMap.put("busarea",
                    accountDTO.getBusAreaName() == null ? "" : accountDTO.getBusAreaName());
            dataMap.put("dept",
                    accountDTO.getTeleDeptName() == null ? "" : accountDTO.getTeleDeptName());
            dataMap.put("group",
                    accountDTO.getTeleGorupName() == null ? "" : accountDTO.getTeleGorupName());
            dataMap.put("sale",
                    accountDTO.getTeleSaleName() == null ? "" : accountDTO.getTeleSaleName());
            dataMap.put("receivable", accountDTO.getSignAmountReceivable() == null ? ""
                    : accountDTO.getSignAmountReceivable());
            // if (accountDTO.getPayType() == 1 || accountDTO.getPayType() == 2) {
            dataMap.put("toll", accountDTO.getFirstToll() == null ? "" : accountDTO.getFirstToll());
            dataMap.put("preferent", accountDTO.getPreferentialAmount() == null ? ""
                    : accountDTO.getPreferentialAmount());
            /*
             * } else { dataMap.put("toll", ""); dataMap.put("preferent", ""); }
             */
            dataMap.put("settle",
                    accountDTO.getSettlementMoney() == null ? "" : accountDTO.getSettlementMoney());

            dataMap.put("amount", accountDTO.getAmountPerformance() == null ? ""
                    : accountDTO.getAmountPerformance());
            dataMap.put("giveAmount",
                    accountDTO.getGiveAmount() == null ? "" : accountDTO.getGiveAmount());
        }
        File file = createDoc(dataMap);
        response.setContentType("text/html;charset=utf-8");
        request.setCharacterEncoding("UTF-8");
        java.io.BufferedInputStream bis = null;
        java.io.BufferedOutputStream bos = null;

        try {
            long fileLength = file.length();
            response.setContentType("application/msword");
            response.setHeader("Content-disposition", "attachment; filename="
                    + URLEncoder.encode(dataMap.get("statementNo") + ".doc", "utf-8"));
            response.setHeader("Content-Length", String.valueOf(fileLength));
            bis = new BufferedInputStream(new FileInputStream(file));
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                bis.close();
            }
            if (bos != null) {
                bos.close();
            }
            File directory = new File("");
            boolean success = (new File(directory.getCanonicalPath() + "/"
                    + dataMap.get("statementNo").toString() + ".doc")).delete();
        }
        return null;
    }


    private File createDoc(Map dataMap) {
        // 获取模板
        Configuration configuration = new Configuration();
        configuration.setDefaultEncoding("utf-8");
        configuration.setClassForTemplateLoading(this.getClass(), "/excel-templates");
        Template t = null;

        String name = dataMap.get("statementNo") + ".doc";
        File file = new File(name);
        try {
            t = configuration.getTemplate("balanceaccount.ftl");
            t.setEncoding("UTF-8");

            Writer out =
                    new BufferedWriter(new OutputStreamWriter(new FileOutputStream(name), "UTF-8"));
            t.process(dataMap, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 申请页面
     * 
     * @return
     */
    @RequestMapping("/settleAccounts")
    // @RequiresPermissions("financing:balanceaccountManager:view")
    public String settleAccounts(HttpServletRequest request) {
        PayDetailAccountDTO queryDTO = new PayDetailAccountDTO();
        queryDTO.setPayDetailId(Long.parseLong(request.getParameter("payDetailId")));
        JSONResult<PayDetailAccountDTO> jsonResult =
                balanceAccountApplyClient.getPayDetailById(queryDTO);
        Map dataMap = new HashMap<>();
        PayDetailAccountDTO accountDTO = new PayDetailAccountDTO();
        if (JSONResult.SUCCESS.equals(jsonResult.getCode()) && jsonResult.getData() != null) {
            List<DictionaryItemRespDTO> dictionaryItemRespDTOs =
                    getDictionaryByCode(DicCodeEnum.VISITSTORETYPE.getCode());
            List<DictionaryItemRespDTO> payModeItem =
                    getDictionaryByCode(DicCodeEnum.PAYMODE.getCode());

            accountDTO = jsonResult.getData();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            List<DictionaryItemRespDTO> giveTypeDTOs = getDictionaryByCode(Constants.GIVE_TYPE);
            dataMap.put("signShopType", "");
            dataMap.put("giveType", "");
            // 去字典表查询签约店型
            if (dictionaryItemRespDTOs != null && dictionaryItemRespDTOs.size() > 0) {
                for (DictionaryItemRespDTO dictionaryItemRespDTO : dictionaryItemRespDTOs) {
                    if (dictionaryItemRespDTO.getValue().equals(accountDTO.getSignShopType())) {
                        accountDTO.setSignShopType(dictionaryItemRespDTO.getName());
                    }
                }
            }
            // 去字典表查询赠送类型
            if (giveTypeDTOs != null && giveTypeDTOs.size() > 0) {
                for (DictionaryItemRespDTO dictionaryItemRespDTO : giveTypeDTOs) {
                    if (accountDTO.getGiveType() != null && dictionaryItemRespDTO.getValue()
                            .equals(accountDTO.getGiveType().toString())) {
                        accountDTO.setGiveTypeName(dictionaryItemRespDTO.getName());
                    }
                }
            }

            String payMode1 = accountDTO.getPayMode();
            String payMode = "";
            if (StringUtils.isNotBlank(payMode1)) {
                String[] split = payMode1.split(",");
                for (int i = 0; i < split.length; i++) {
                    for (DictionaryItemRespDTO item : payModeItem) {
                        if (item.getValue().equals(split[i])) {
                            if (i == 0) {
                                payMode = item.getName();
                            } else {
                                payMode = payMode + "," + item.getName();
                            }
                        }
                    }
                }
            }

            // String payMode = "";
            // if (accountDTO.getPayMode() == 1) {
            // payMode = "现金";
            // } else if (accountDTO.getPayMode() == 2) {
            // payMode = "POS";
            // } else if (accountDTO.getPayMode() == 3) {
            // payMode = "转账";
            // } else if (accountDTO.getPayMode() == 4) {
            // payMode = "微信";
            // } else if (accountDTO.getPayMode() == 5) {
            // payMode = "支付宝";
            // }
            accountDTO.setPayModes(payMode);
            String payType = "";
            if (accountDTO.getPayType() == 1) {
                payType = "全款";
            } else if (accountDTO.getPayType() == 2) {
                payType = "定金";
            } else if (accountDTO.getPayType() == 3) {
                payType = "追加定金";
            } else if (accountDTO.getPayType() == 4) {
                payType = "尾款";
            }
            accountDTO.setGiveAmount(accountDTO.getGiveAmount());
            accountDTO.setPayTypes(payType);
            String createTime = sdf.format(accountDTO.getPayTime());
            accountDTO.setPayTypes(payType);
            accountDTO.setDay(createTime.substring(8, 10));
            accountDTO.setMonth(createTime.substring(5, 7));
            accountDTO.setYear(createTime.substring(0, 4));
            /*
             * if (accountDTO.getPayType() != 1 && accountDTO.getPayType() != 2) {
             * accountDTO.setFirstToll(null); accountDTO.setPreferentialAmount(null); }
             */
        }
        request.setAttribute("accountDTO", accountDTO);
        return "financing/settleAccountsPage";
    }

    /***
     * 获取商务小组商务经理电销小组电销顾问
     * 
     * @return
     */
    @PostMapping("/getBusAndTel")
    @ResponseBody
    public Map getBusAndTel(@RequestBody ReconciliationConfirmReq req, HttpServletRequest request) {
        UserInfoDTO user = CommUtil.getCurLoginUser();
        // 查询所有电销事业部
        List<OrganizationRespDTO> teleCompanyList =
                getOrgList(null, OrgTypeConstant.DXFGS, user.getBusinessLine());
        // 查询所有商务组
        List<OrganizationRespDTO> busGroupList =
                getOrgList(null, OrgTypeConstant.SWZ, user.getBusinessLine());
        request.setAttribute("busGroupList", busGroupList);
        Long busGrouptdate = System.currentTimeMillis();
        // 查询所有电销组
        List<OrganizationRespDTO> teleGroupList =
                getOrgList(null, OrgTypeConstant.DXZ, user.getBusinessLine());
        request.setAttribute("teleGroupList", teleGroupList);
        Long teleGroupdate = System.currentTimeMillis();
        // 查询所有商务经理
        List<UserInfoDTO> busSaleList =
                getUserList(null, RoleCodeEnum.SWJL.name(), null, user.getBusinessLine());
        request.setAttribute("busSaleList", busSaleList);
        Long busSaledate = System.currentTimeMillis();
        logger.info("所有商务经理查询时间" + (busSaledate - teleGroupdate));
        // 查询所有电销创业顾问
        List<UserInfoDTO> teleSaleList =
                getUserList(null, RoleCodeEnum.DXCYGW.name(), null, user.getBusinessLine());

        request.setAttribute("teleSaleList", teleSaleList);
        Map map = new HashMap();
        map.put("busGroupList", busGroupList);
        map.put("teleGroupList", teleGroupList);
        map.put("busSaleList", busSaleList);
        map.put("teleSaleList", teleSaleList);
        map.put("teleCompanyList", teleCompanyList);
        return map;
    }

    /**
     * 校验（提交对账时增加约束）
     */
    @RequestMapping("/validateBalance")
    @ResponseBody
    public JSONResult<String> validateBalance(@RequestBody ReconciliationConfirmReq req){
        return reconciliationConfirmFeignClient.validateBalance(req);
    }


    private String getTimeStr(Date date) {
        if (date == null) {
            return "";
        }
        return DateUtil.convert2String(date, DateUtil.ymd);
    }

}
