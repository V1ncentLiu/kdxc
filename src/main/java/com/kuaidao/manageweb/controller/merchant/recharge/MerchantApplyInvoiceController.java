package com.kuaidao.manageweb.controller.merchant.recharge;

import com.kuaidao.manageweb.feign.merchant.recharge.MerchantApplyInvoiceFeignClient;
import com.kuaidao.manageweb.feign.merchant.recharge.MerchantRechargePreferentialFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created on: 2019-09-23-17:47
 */
@Controller
@RequestMapping("/merchant/merchantApplyInvoice")
public class MerchantApplyInvoiceController {

    private static Logger logger = LoggerFactory.getLogger(MerchantApplyInvoiceController.class);

    @Autowired
    private MerchantApplyInvoiceFeignClient merchantApplyInvoiceFeignClient;


}
