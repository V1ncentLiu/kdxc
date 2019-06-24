package com.kuaidao.manageweb.controller.financing;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.kuaidao.aggregation.constant.AggregationConstant;
import com.kuaidao.aggregation.dto.financing.ReconciliationConfirmDTO;
import com.kuaidao.aggregation.dto.financing.ReconciliationConfirmPageParam;
import com.kuaidao.aggregation.dto.financing.ReconciliationConfirmReq;
import com.kuaidao.aggregation.dto.paydetail.PayDetailAccountDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
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
        // 查询所有商务大区
        List<OrganizationRespDTO> busAreaList =
                getOrgList(null, OrgTypeConstant.SWDQ, user.getBusinessLine());
        request.setAttribute("busAreaList", busAreaList);
        // 查询所有商务组
        List<OrganizationRespDTO> busGroupList =
                getOrgList(null, OrgTypeConstant.SWZ, user.getBusinessLine());
        request.setAttribute("busGroupList", busGroupList);
        // 查询所有电销事业部
        List<OrganizationRespDTO> teleDeptList =
                getOrgList(null, OrgTypeConstant.DZSYB, user.getBusinessLine());
        request.setAttribute("teleDeptList", teleDeptList);
        // 查询所有电销组
        List<OrganizationRespDTO> teleGroupList =
                getOrgList(null, OrgTypeConstant.DXZ, user.getBusinessLine());
        request.setAttribute("teleGroupList", teleGroupList);
        // 查询所有商务经理
        List<UserInfoDTO> busSaleList =
                getUserList(null, RoleCodeEnum.SWJL.name(), null, user.getBusinessLine());
        request.setAttribute("busSaleList", busSaleList);
        // 查询所有电销创业顾问
        List<UserInfoDTO> teleSaleList =
                getUserList(null, RoleCodeEnum.DXCYGW.name(), null, user.getBusinessLine());
        request.setAttribute("teleSaleList", teleSaleList);

        // 查询所有项目
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();
        request.setAttribute("projectList", allProject.getData());
        // 查询所有省
        JSONResult<List<SysRegionDTO>> getproviceList = sysRegionFeignClient.getproviceList();
        request.setAttribute("provinceList", getproviceList.getData());
        // 根据角色查询页面字段
        QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq = new QueryFieldByRoleAndMenuReq();
        queryFieldByRoleAndMenuReq.setMenuCode("financing:balanceaccountManager");
        queryFieldByRoleAndMenuReq.setId(user.getRoleList().get(0).getId());
        JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu =
                customFieldFeignClient.queryFieldByRoleAndMenu(queryFieldByRoleAndMenuReq);
        request.setAttribute("fieldList", queryFieldByRoleAndMenu.getData());

        // 根据用户查询页面字段
        QueryFieldByUserAndMenuReq queryFieldByUserAndMenuReq = new QueryFieldByUserAndMenuReq();
        queryFieldByUserAndMenuReq.setId(user.getId());
        queryFieldByUserAndMenuReq.setRoleId(user.getRoleList().get(0).getId());
        queryFieldByUserAndMenuReq.setMenuCode("financing:balanceaccountManager");
        JSONResult<List<UserFieldDTO>> queryFieldByUserAndMenu =
                customFieldFeignClient.queryFieldByUserAndMenu(queryFieldByUserAndMenuReq);
        request.setAttribute("userFieldList", queryFieldByUserAndMenu.getData());
        // 查询签约店型集合
        request.setAttribute("vistitStoreTypeList",
                getDictionaryByCode(DicCodeEnum.VISITSTORETYPE.getCode()));
        request.setAttribute("payModeItem", getDictionaryByCode(DicCodeEnum.PAYMODE.getCode()));
        return "financing/balanceAccountPage";
    }

    /***
     * 对账结算申请列表
     * 
     * @return
     */
    @PostMapping("/applyList")
    @ResponseBody
    @RequiresPermissions("financing:balanceaccountManager:view")
    public JSONResult<PageBean<ReconciliationConfirmDTO>> appayList(
            @RequestBody ReconciliationConfirmPageParam pageParam, HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        pageParam.setUserId(user.getId());
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {

            pageParam.setRoleCode(roleList.get(0).getRoleCode());
        }
        pageParam.setBusinessLine(user.getBusinessLine());
        JSONResult<PageBean<ReconciliationConfirmDTO>> list =
                reconciliationConfirmFeignClient.applyList(pageParam);
        return list;
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
        req.setCommissionMoney(
                bigDecimal.multiply(new BigDecimal(req.getRatio())).divide(new BigDecimal(100)));
        req.setStatus(AggregationConstant.RECONCILIATION_STATUS.STATUS_2);
        JSONResult<Void> reconciliationConfirm = reconciliationConfirmFeignClient.applyConfirm(req);
        return reconciliationConfirm;
    }

    /**
     * 根据对账申请表id获取已对账的佣金之和
     * 
     * @author: Fanjd
     * @param accountId 对账申请表主键
     * @return: com.kuaidao.common.entity.JSONResult<java.lang.Void>
     * @Date: 2019/6/14 18:25
     * @since: 1.0.0
     **/
    @ResponseBody
    @PostMapping("/getConfirmCommission")
    public JSONResult<BigDecimal> getConfirmCommission(
            @RequestBody ReconciliationConfirmReq reconciliationConfirmReq) {
        JSONResult<BigDecimal> sumConfirmCommission = reconciliationConfirmFeignClient
                .getConfirmCommission(reconciliationConfirmReq.getSignId());
        return sumConfirmCommission;
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
            if(StringUtils.isNotBlank(payMode1)){
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
            if(StringUtils.isNotBlank(payMode1)){
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

}
