package com.kuaidao.manageweb.controller.merchant.resourcetrajectory;

import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.controller.merchant.pubcustomer.PubcustomerController;
import com.kuaidao.merchant.dto.pubcusres.ClueReceiveRecordsDTO;
import com.kuaidao.merchant.dto.resourcetrajectory.ResourceTrajectoryDTO;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
  @RequestMapping("/topage1")
  public String listPage1(HttpServletRequest request) {
    return "merchant/resourceTrajectory/resourceTrajectory";
  }
  /**
   * 页面数据接口
   */

  @ResponseBody
  @RequestMapping("/data")
  public JSONResult<ResourceTrajectoryDTO> data() {
    ResourceTrajectoryDTO resourceTrajectory = new ResourceTrajectoryDTO();


    return new JSONResult().success(resourceTrajectory);
  }


}
