package com.kuaidao.manageweb.controller.merchant.merchantsetmeal;

import com.kuaidao.account.dto.mservice.MerchantServiceDTO;
import com.kuaidao.account.dto.mservice.MerchantServiceReq;
import com.kuaidao.callcenter.dto.seatmanager.SeatManagerReq;
import com.kuaidao.callcenter.dto.seatmanager.SeatManagerResp;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.component.merchant.MerchantComponent;
import com.kuaidao.manageweb.feign.merchant.mserviceshow.MserviceShowFeignClient;
import com.kuaidao.manageweb.feign.merchant.seatmanager.SeatManagerFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

  @Autowired
  private MserviceShowFeignClient mserviceShowFeignClient;

  @Autowired
  private SeatManagerFeignClient seatManagerFeignClient;


  /**
   * 跳转商家服务列表
   * @param request
   * @return
   */
  @RequestMapping("/toPage")
  // @RequiresPermissions("merchant:setMeal:view")
  public String toPage(HttpServletRequest request) {
    // 商家主账号
    List<UserInfoDTO> merchantUser = merchantComponent
        .getMerchantUser(SysConstant.USER_TYPE_TWO, null);
//    // 商家子账户
//    List<UserInfoDTO> userList = merchantComponent.getMerchantUser(SysConstant.USER_TYPE_THREE,null);
//    merchantUser.addAll(userList);
    request.setAttribute("userList", merchantUser);
    return "merchant/serviceManagement/serviceManagement";
  }

  /**
   * 查询列表数据
   */
  @PostMapping("/queryList")
  @ResponseBody
  public JSONResult<List<MerchantServiceDTO>> queryList(@RequestBody MerchantServiceReq merchantServiceReq){
    // 查询坐席
    if(StringUtils.isNotEmpty(merchantServiceReq.getSeatNo())){
      SeatManagerReq seatManagerReq = new SeatManagerReq();
      seatManagerReq.setSeatNo(merchantServiceReq.getSeatNo());
      JSONResult<List<SeatManagerResp>> result = seatManagerFeignClient
          .queryListNoPage(seatManagerReq);
      if(CommonUtil.resultCheck(result)){
        List<SeatManagerResp> data = result.getData();
        List<Long> pacList = new ArrayList<>();
        List<Long> idList = new ArrayList<>();
        for(SeatManagerResp seatManager : data){
          pacList.add(seatManager.getPackageId());
          idList.add(seatManager.getBuyPackageId());
        }
        merchantServiceReq.setIdList(idList);
        merchantServiceReq.setPacIdList(pacList);
      }
    }
    JSONResult<List<MerchantServiceDTO>> result = mserviceShowFeignClient
        .queryList(merchantServiceReq);
    JSONResult<List<SeatManagerResp>> result1 = seatManagerFeignClient.countSeatNumOnMerchent();
    if(CommonUtil.resultCheck(result)&&CommonUtil.resultCheck(result1)){
      // 设置对应坐席数量
      Map<String, Integer> collect = result1.getData().stream().collect(Collectors
          .toMap(a -> a.getBuyPackageId() + "-" + a.getPackageId(),
              b -> b.getSeatNum()));
      for(MerchantServiceDTO service:result.getData()){
        service.setSeatNum(collect.get(service.getId() + "-" + service.getPackageId()));
      }
    }
    return result;
  }



}
