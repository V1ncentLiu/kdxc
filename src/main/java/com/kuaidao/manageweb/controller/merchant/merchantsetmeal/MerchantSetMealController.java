package com.kuaidao.manageweb.controller.merchant.merchantsetmeal;

import com.kuaidao.manageweb.component.merchant.MerchantComponent;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Auther: admin
 * @Date: 2019/10/7 14:49
 * @Description:
 */

@Controller
@RequestMapping("/merchant/mechantSetMeal")
public class MerchantSetMealController {
  private static Logger logger = LoggerFactory.getLogger(MerchantSetMealController.class);

  @Autowired
  private MerchantComponent merchantComponent;


  /**
   * 跳转商家服务列表
   * @param request
   * @return
   */
  @RequestMapping("/toPage")
  @RequiresPermissions("merchant:setMeal:view")
  public String toPage(HttpServletRequest request) {
    // 商家主账号
    List<UserInfoDTO> userList = merchantComponent.getMerchantUser(null,null);
    request.setAttribute("userList", userList);
    return "merchant/serviceManagement/serviceManagement";
  }
  /**
   * 查询列表数据
   */


}
