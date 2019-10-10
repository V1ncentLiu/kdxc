package com.kuaidao.manageweb.controller.merchant.bussinesscall;

import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description: BussinessCallCostController
 * @date: 2019/10/9 17:13
 * @author: xuyunfeng
 * @version: 1.0
 */
@Controller
@RequestMapping("/merchant/bussinessCallCost")
public class BussinessCallCostController {

  /**
  * @Description 商家通话费用页面初始化
  * @param request
  * @Return java.lang.String
  * @Author xuyunfeng
  * @Date 2019/10/9 17:15
  **/
  @RequestMapping("/initBussinessCallCost")
  @RequiresPermissions("merchant:bussinessCallCost:view")
  public String initBussinessCallCost(HttpServletRequest request) {

    return "merchant/bussinessCall/chargeRecord";
  }
}
