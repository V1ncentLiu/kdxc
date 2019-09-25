package com.kuaidao.manageweb.controller.merchant.recharge;

import com.kuaidao.common.constant.ComConstant.INVOICE_APPLY_STATUS;
import com.kuaidao.common.constant.ComConstant.USER_STATUS;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.feign.merchant.recharge.MerchantApplyInvoiceFeignClient;
import com.kuaidao.manageweb.feign.merchant.recharge.MerchantRechargePreferentialFeignClient;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.merchant.dto.recharge.MerchantApplyInvoiceDTO;
import com.kuaidao.merchant.dto.recharge.MerchantApplyInvoiceReq;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created on: 2019-09-23-17:47
 */
@Controller
@RequestMapping("/merchant/merchantApplyInvoice")
public class MerchantApplyInvoiceController {

    private static Logger logger = LoggerFactory.getLogger(MerchantApplyInvoiceController.class);

    @Autowired
    private MerchantApplyInvoiceFeignClient merchantApplyInvoiceFeignClient;
    @Autowired
    private MerchantUserInfoFeignClient merchantUserInfoFeignClient;


    /***
     * 发票记录页面
     *
     * @return
     */
    @RequestMapping("/initApplyInvoiceRecord")
    @RequiresPermissions("merchant:merchantApplyInvoice:view")
    public String initApplyInvoiceRecord(HttpServletRequest request) {
        //获取所有非禁用商家主账号
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        List<Integer> list = new ArrayList<>();
        list.add(USER_STATUS.ENABLE, USER_STATUS.LOCK);
        userInfoDTO.setStatusList(list);
        userInfoDTO.setUserType(Constants.MERCHANT_PRIMARY_ACCOUNT);
        JSONResult<List<UserInfoDTO>> userInfoDTOList = merchantUserInfoFeignClient
            .merchantUserList(userInfoDTO);
        if (CollectionUtils.isNotEmpty(userInfoDTOList.getData())) {
            request.setAttribute("userList",
                userInfoDTOList);
        }
        return "merchant/merchantReceiptRecord/merchantReceiptRecord";
    }

    /***
     * 管理端查询发票记录
     *
     * @return
     */
    @RequestMapping("/findMerchantApplyInvoiceList")
    @ResponseBody
    public JSONResult<PageBean<MerchantApplyInvoiceDTO>> findMerchantApplyInvoiceList(
        MerchantApplyInvoiceReq req) {
        return merchantApplyInvoiceFeignClient.findMerchantApplyInvoiceList(req);
    }

    /***
     * 标记发票已开
     *
     * @return
     */
    @RequestMapping("/markInvoiceIssued")
    @ResponseBody
    public JSONResult markInvoiceIssued(MerchantApplyInvoiceReq req) {
        req.setStatus(INVOICE_APPLY_STATUS.HAVE_ISSUE);
        return merchantApplyInvoiceFeignClient.updateApplyInvoice(req);
    }
}
