package com.kuaidao.manageweb.controller.merchant.seatmanager;

import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.component.merchant.MerchantComponent;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.*;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Auther: admin
 * @Date: 2019/10/4 10:22
 * @Description: 坐席管理
 */
@Controller
@RequestMapping("/merchant/seatManager")
public class SeatManagerController {
  private static Logger logger = LoggerFactory.getLogger(SeatManagerController.class);

  @Autowired
  private MerchantComponent merchantComponent;
  /**
   * 跳转作息管理列表
   * @param request
   * @return
   */
  @RequestMapping("/toPage")
  @RequiresPermissions("merchant:seatMagager:view")
  public String toPage(HttpServletRequest request, @RequestBody Long userId) {
   // UserInfoDTO merchantById = merchantComponent.getMerchantById(userId);
    // 商家主账号
    List<UserInfoDTO> userList = merchantComponent.getMerchantSubUser(userId,null);
    request.setAttribute("userList", userList);
    return "merchant/mechantSetMeal/list";
  }

  /**
   *  获取商家子账户+商家账户
   */
  @ResponseBody
  @PostMapping("/merchantUsers")
  public  List<UserInfoDTO> merchantUsers(HttpServletRequest request, IdEntityLong id) {
    UserInfoDTO merchantUser = merchantComponent.getMerchantById(id.getId());
    List<UserInfoDTO> userList = merchantComponent.getMerchantSubUser(id.getId(),null);
    userList.add(merchantUser);
    return userList;
  }

  /**
   *  新增
   */

  /**
   * 更新
   */

  /**
   * 删除
   */

  /**
   * 查询
   */

}
