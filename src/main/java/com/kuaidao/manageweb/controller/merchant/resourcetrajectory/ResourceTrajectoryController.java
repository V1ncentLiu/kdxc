package com.kuaidao.manageweb.controller.merchant.resourcetrajectory;

import com.kuaidao.manageweb.controller.merchant.pubcustomer.PubcustomerController;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Auther: admin
 * @Date: 2019/9/27 10:36
 * @Description:
 */
@Controller
@RequestMapping("/merchant/resourceTrajectory")
public class ResourceTrajectoryController {
  private static Logger logger = LoggerFactory.getLogger(ResourceTrajectoryController.class);
  /**
   * 页面跳转
   */
  @RequestMapping("/topage")
  public String listPage(HttpServletRequest request) {
    return "merchant/resourceTrajectory/resourceTrajectory";
  }
}
