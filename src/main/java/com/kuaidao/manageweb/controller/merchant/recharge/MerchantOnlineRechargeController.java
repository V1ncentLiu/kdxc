package com.kuaidao.manageweb.controller.merchant.recharge;

import com.kuaidao.account.dto.recharge.*;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.feign.merchant.recharge.MerchantRechargePreferentialFeignClient;
import com.kuaidao.manageweb.feign.merchant.recharge.MerchantRechargeRecordBusinessFeignClient;
import com.kuaidao.manageweb.feign.merchant.recharge.MerchantUserAccountFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

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
  @Autowired
  private MerchantRechargeRecordBusinessFeignClient merchantRechargeRecordBusinessFeignClient;
  /**
  * @Description 加载在线充值页面
  * @param request
  * @Return java.lang.String
  * @Author xuyunfeng
  * @Date 2019/9/24 16:07
  **/
  @RequestMapping("/initOnlineRecharge")
  @RequiresPermissions("merchant:merchantOnlineRecharge:add")
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
      MerchantUserAccountDTO merchantUserAccountDTO = new MerchantUserAccountDTO();
      merchantUserAccountDTO = accountDTOJSONResult.getData();
      if(merchantUserAccountDTO == null || merchantUserAccountDTO.getBalance() == null){
        merchantUserAccountDTO.setBalance(new BigDecimal("0.00"));
      }
      if(merchantUserAccountDTO == null || merchantUserAccountDTO.getTotalAmounts() == null){
        merchantUserAccountDTO.setTotalAmounts(new BigDecimal("0.00"));
      }
      request.setAttribute("merchantUserAccountDTO",merchantUserAccountDTO);
    }catch (Exception e){
      logger.error("加载在线充值页面initOnlineRecharge:{}",e);
    }
    return "merchant/payment/paymentOnline";
  }
  /**
  * @Description 获取支付宝、微信支付URL
  * @param req
  * @Return com.kuaidao.common.entity.JSONResult<com.kuaidao.account.dto.recharge.MerchantRechargeResp>
  * @Author xuyunfeng
  * @Date 2019/9/28 15:52
  **/
  @ResponseBody
  @RequestMapping("/getWeChatAndAlipayCode")
  @RequiresPermissions("merchant:merchantOnlineRecharge:add")
  public JSONResult<MerchantRechargeResp> getWeChatAndAlipayCode(@RequestBody MerchantRechargeReq req,HttpServletRequest request){
    try {
      UserInfoDTO user = CommUtil.getCurLoginUser();
      req.setRechargeBusiness(user.getId());
      JSONResult<MerchantRechargeResp> jsonResult = merchantRechargeRecordBusinessFeignClient.getWeChatAndAlipayCode(req);
      return jsonResult;
    }catch (Exception e){
      logger.error("加载在线充值页面getWeChatAndAlipayCode:{}",e);
      return new JSONResult<MerchantRechargeResp>().fail(SysErrorCodeEnum.ERR_SYSTEM.getCode(),"getWeChatAndAlipayCode接口异常");
    }
  }
  /**
  * @Description 支付宝跳转URL
  * @param
  * @Return java.lang.String
  * @Author xuyunfeng
  * @Date 2019/10/9 9:43
  **/
  @GetMapping("/toAlipayPage")
  public String toAlipayPage(HttpServletRequest request){
    //查询优惠信息
    MerchantRechargePreferentialReq req = new MerchantRechargePreferentialReq();
    JSONResult<List<MerchantRechargePreferentialDTO>> jsonResult = merchantRechargePreferentialFeignClient.findAllRechargePreferential(req);
    List<MerchantRechargePreferentialDTO> preferentialDTOList = jsonResult.getData();
    request.setAttribute("preferentialDTOList",preferentialDTOList);
    request.setAttribute("rechargeWay", Constants.RECHARGE_WAY_ALI_PAY);
    return "merchant/payment/paymentOnlineCutdown";
  }
  /**
   * @Description 微信跳转URL
   * @param
   * @Return java.lang.String
   * @Author xuyunfeng
   * @Date 2019/10/9 9:43
   **/
  @GetMapping("/toWechatPage")
  public String toWechatPage(HttpServletRequest request){
    //查询优惠信息
    MerchantRechargePreferentialReq req = new MerchantRechargePreferentialReq();
    JSONResult<List<MerchantRechargePreferentialDTO>> jsonResult = merchantRechargePreferentialFeignClient.findAllRechargePreferential(req);
    List<MerchantRechargePreferentialDTO> preferentialDTOList = jsonResult.getData();
    request.setAttribute("preferentialDTOList",preferentialDTOList);
    request.setAttribute("rechargeWay", Constants.RECHARGE_WAY_WEXIN_PAY);
      return "merchant/payment/paymentResult";
  }

  /**
  * @Description 检查支付状态
  * @param req
  * @Return java.lang.String
  * @Author xuyunfeng
  * @Date 2019/10/9 19:35
  **/
  @ResponseBody
  @RequestMapping("/checkPayStatus")
  public JSONResult<Integer> checkPayStatus(@RequestBody MerchantRechargeReq req){
    JSONResult<MerchantRechargeRecordDTO> list = merchantRechargeRecordBusinessFeignClient.getMerchantRechargeRecordInfo(req);
    MerchantRechargeRecordDTO  merchantRechargeRecordDTO = list.getData();
      return new JSONResult<Integer>().success(merchantRechargeRecordDTO.getRechargeStatus());
  }
}
