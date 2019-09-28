package com.kuaidao.manageweb.controller.merchant.recharge;

import com.kuaidao.account.dto.recharge.MerchantRechargePreferentialDTO;
import com.kuaidao.account.dto.recharge.MerchantRechargePreferentialReq;
import com.kuaidao.account.dto.recharge.MerchantRechargeReq;
import com.kuaidao.account.dto.recharge.MerchantRechargeResp;
import com.kuaidao.account.dto.recharge.MerchantUserAccountDTO;
import com.kuaidao.account.dto.recharge.MerchantUserAccountQueryDTO;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.feign.merchant.recharge.MerchantRechargePreferentialFeignClient;
import com.kuaidao.manageweb.feign.merchant.recharge.MerchantUserAccountFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description: MerchantOnlineRechargeController在线充值类
 * @date: 2019/9/24 16:04
 * @author: xuyunfeng
 * @version: 1.0
 */
@Controller
@RequestMapping("/merchant/merchantOnlineRecharge")
public class MerchantOnlineRechargeController {
  private static Logger logger = LoggerFactory.getLogger(MerchantOnlineRechargeController.class);

  @Autowired
  private MerchantRechargePreferentialFeignClient merchantRechargePreferentialFeignClient;
  @Autowired
  private MerchantUserAccountFeignClient merchantUserAccountFeignClient;
  /**
  * @Description 加载在线充值页面
  * @param request
  * @Return java.lang.String
  * @Author xuyunfeng
  * @Date 2019/9/24 16:07
  **/
  @RequestMapping("/initOnlineRecharge")
  public String initMerchantOnlineRecharge(HttpServletRequest request){
    try {
      //查询优惠信息
      MerchantRechargePreferentialReq req = new MerchantRechargePreferentialReq();
      JSONResult<List<MerchantRechargePreferentialDTO>> jsonResult = merchantRechargePreferentialFeignClient.findAllRechargePreferential(req);
      List<MerchantRechargePreferentialDTO> preferentialDTOList = jsonResult.getData();
      request.setAttribute("preferentialDTOList",preferentialDTOList);
      //查询商家账号余额信息
      UserInfoDTO user = CommUtil.getCurLoginUser();
      MerchantUserAccountQueryDTO dto = new MerchantUserAccountQueryDTO();
      dto.setUserId(user.getId());
      JSONResult<MerchantUserAccountDTO> accountDTOJSONResult = merchantUserAccountFeignClient.getMerchantUserAccountInfo(dto);
      request.setAttribute("MerchantUserAccountDTO",accountDTOJSONResult.getData());
    }catch (Exception e){
      logger.error("加载在线充值页面initOnlineRecharge:{}",e);
    }
    return null;
  }
  @RequestMapping("/getWeChatAndAlipayCode")
  public JSONResult<MerchantRechargeResp> getWeChatAndAlipayCode(@RequestBody MerchantRechargeReq req){

    try {

    }catch (Exception e){
      logger.error("加载在线充值页面initOnlineRecharge:{}",e);
    }
    return null;
  }
}
