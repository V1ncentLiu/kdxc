package com.kuaidao.manageweb.controller.merchant.messcenter;

import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.controller.merchant.cpoolreceive.CpoolRecevieController;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Auther: admin
 * @Date: 2019/10/2 16:39
 * @Description:
 */
@Controller
@RequestMapping("/merchant/messcenter")
public class MerchantMessCenterController {

  private static Logger logger = LoggerFactory.getLogger(MerchantMessCenterController.class);
  /**
   * 跳转：list页面
   */
  @RequestMapping("/topage")
  public String toList( HttpServletRequest request) {
    return "merchant/merchant_messageCenter/messageCenter";
  }

}
