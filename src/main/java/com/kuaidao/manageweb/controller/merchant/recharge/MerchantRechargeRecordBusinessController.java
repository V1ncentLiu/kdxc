package com.kuaidao.manageweb.controller.merchant.recharge;

import com.kuaidao.account.dto.recharge.MerchantApplyInvoiceReq;
import com.kuaidao.account.dto.recharge.MerchantRechargeRecordDTO;
import com.kuaidao.account.dto.recharge.MerchantRechargeRecordQueryDTO;
import com.kuaidao.account.dto.recharge.RechargeAccountDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.merchant.recharge.MerchantRechargeRecordBusinessFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * @description: MerchantRechargeRecordBusinessController
 * @date: 2019/9/26 15:48
 * @author: xuyunfeng
 * @version: 1.0
 */
@Controller
@RequestMapping("/merchant/merchantRechargeRecordBusiness")
public class MerchantRechargeRecordBusinessController {
  private static Logger logger = LoggerFactory.getLogger(MerchantRechargeRecordBusinessController.class);

  @Autowired
  MerchantRechargeRecordBusinessFeignClient merchantRechargeRecordBusinessFeignClient;

  /**
  * @Description 初始化商家端充值记录页面
  * @param request
  * @Return java.lang.String
  * @Author xuyunfeng
  * @Date 2019/9/26 15:57
  **/
  @RequestMapping("/initRechargeRecordBusiness")
  @RequiresPermissions("merchant:merchantRechargeRecordBusiness:view")
  public String initRechargeRecordBusiness(HttpServletRequest request){
    try {
      UserInfoDTO user = CommUtil.getCurLoginUser();
      MerchantRechargeRecordQueryDTO queryDTO = new MerchantRechargeRecordQueryDTO();
      queryDTO.setRechargeBusiness(user.getId());
      JSONResult<RechargeAccountDTO> rechargeAccountDTOJSONResult = merchantRechargeRecordBusinessFeignClient.getRechargeMoney(queryDTO);
      RechargeAccountDTO rechargeAccountDTO = new RechargeAccountDTO();
      rechargeAccountDTO = rechargeAccountDTOJSONResult.getData();
      if(rechargeAccountDTO == null || rechargeAccountDTO.getTotalRechargeMoney() == null){
        rechargeAccountDTO.setTotalRechargeMoney(new BigDecimal("0"));
      }
      if(rechargeAccountDTO == null || rechargeAccountDTO.getTotalGivenMoney() == null){
        rechargeAccountDTO.setTotalGivenMoney(new BigDecimal("0"));
      }
      if(rechargeAccountDTO == null || rechargeAccountDTO.getTotalAmounts() == null){
        rechargeAccountDTO.setTotalAmounts(new BigDecimal("0"));
      }
      request.setAttribute("rechargeAccountDTO",rechargeAccountDTO);
    }catch (Exception e){
      logger.error("initRechargeRecordBusiness:{}",e);
    }
    return "merchant/merchantRechargeRecord/merchantRechargeRecord";
  }
  /**
  * @Description 商家端充值记录列表查询
  * @param queryDTO
  * @Return com.kuaidao.common.entity.JSONResult<com.kuaidao.common.entity.PageBean<com.kuaidao.account.dto.recharge.MerchantRechargeRecordDTO>>
  * @Author xuyunfeng
  * @Date 2019/9/26 16:07
  **/
  @ResponseBody
  @RequestMapping("/queryBusinessPageList")
  @RequiresPermissions("merchant:merchantRechargeRecordBusiness:view")
  public JSONResult<PageBean<MerchantRechargeRecordDTO>> queryBusinessPageList(@RequestBody MerchantRechargeRecordQueryDTO queryDTO ){
    try {
      UserInfoDTO user = CommUtil.getCurLoginUser();
      queryDTO.setRechargeBusiness(user.getId());
      JSONResult<PageBean<MerchantRechargeRecordDTO>> list = merchantRechargeRecordBusinessFeignClient.queryBusinessPageList(queryDTO);
      return list;
    }catch (Exception e){
      logger.error("initRechargeRecordBusiness:{}",e);
      return new JSONResult<PageBean<MerchantRechargeRecordDTO>>().fail(SysErrorCodeEnum.ERR_SYSTEM.getCode(),"queryBusinessPageList接口异常");
    }
  }

  /**
  * @Description 申请发票
  * @param req
  * @Return com.kuaidao.common.entity.JSONResult<java.lang.Boolean>
  * @Author xuyunfeng
  * @Date 2019/9/26 17:41
  **/
  @ResponseBody
  @RequestMapping("/applyInvoice")
  @RequiresPermissions("merchant:merchantRechargeRecordBusiness:view")
  public JSONResult<Boolean> applyInvoice(@RequestBody MerchantApplyInvoiceReq req){
    try {
      UserInfoDTO user = CommUtil.getCurLoginUser();
      req.setApplyUserId(user.getId());
      return merchantRechargeRecordBusinessFeignClient.applyInvoice(req);
    }catch (Exception e){
      logger.error("applyInvoice:{}",e);
      return new JSONResult<Boolean>().fail(SysErrorCodeEnum.ERR_SYSTEM.getCode(),"applyInvoice接口异常");
    }
  }
}
