package com.kuaidao.manageweb.feign.merchant.recharge;

import com.kuaidao.account.dto.recharge.MerchantApplyInvoiceReq;
import com.kuaidao.account.dto.recharge.MerchantRechargeRecordDTO;
import com.kuaidao.account.dto.recharge.MerchantRechargeRecordQueryDTO;
import com.kuaidao.account.dto.recharge.MerchantRechargeReq;
import com.kuaidao.account.dto.recharge.MerchantRechargeResp;
import com.kuaidao.account.dto.recharge.RechargeAccountDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description: MerchantRechargeRecordFeignClient
 * @date: 2019/9/25 19:59
 * @author: xuyunfeng
 * @version: 1.0
 */
@FeignClient(name = "account-service", path = "/account/merchantRechargeBusinessRecord",
    fallback = MerchantRechargeRecordBusinessFeignClient.HystrixClientFallbackBusiness.class)
public interface MerchantRechargeRecordBusinessFeignClient {

  /**
   * @Description 获取商家累计充值总金额、累计赠送总金额、剩余金额
   * @param queryDTO
   * @Return com.kuaidao.common.entity.JSONResult<com.kuaidao.account.dto.recharge.MerchantRechargeRecordDTO>
   * @Author xuyunfeng
   * @Date 2019/9/25 20:32
   **/
  @PostMapping("/getRechargeMoney")
  public JSONResult<RechargeAccountDTO> getRechargeMoney(
      @RequestBody MerchantRechargeRecordQueryDTO queryDTO);

  /**
   * @Description 商家端充值记录列表数据查询
   * @param queryDTO
   * @Return com.kuaidao.common.entity.JSONResult<com.kuaidao.account.dto.recharge.MerchantRechargeRecordDTO>
   * @Author xuyunfeng
   * @Date 2019/9/26 11:39
   **/
  @PostMapping("/queryBusinessPageList")
  public JSONResult<PageBean<MerchantRechargeRecordDTO>> queryBusinessPageList(
      @RequestBody MerchantRechargeRecordQueryDTO queryDTO);
  /**
   * @Description 申请发票
   * @param req
   * @Return com.kuaidao.common.entity.JSONResult<java.lang.Boolean>
   * @Author xuyunfeng
   * @Date 2019/9/26 17:41
   **/
  @RequestMapping("/applyInvoice")
  public JSONResult<Boolean> applyInvoice(@RequestBody MerchantApplyInvoiceReq req);
  /**
   * @Description 获取微信或支付宝的支付URL
   * @param req
   * @Return com.kuaidao.common.entity.JSONResult<com.kuaidao.account.dto.recharge.MerchantRechargeResp>
   * @Author xuyunfeng
   * @Date 2019/9/27 16:48
   **/
  @RequestMapping("/getWeChatAndAlipayCode")
  public JSONResult<MerchantRechargeResp> getWeChatAndAlipayCode(@RequestBody MerchantRechargeReq req);

  /**
   * @Description 查看充值详情
   * @param req
   * @Return com.kuaidao.common.entity.JSONResult<com.kuaidao.account.dto.recharge.MerchantRechargeRecordDTO>
   * @Author xuyunfeng
   * @Date 2019/10/9 14:47
   **/
  @RequestMapping("/getMerchantRechargeRecordInfo")
  public JSONResult<MerchantRechargeRecordDTO> getMerchantRechargeRecordInfo(@RequestBody MerchantRechargeReq req);

  @Component
  static class HystrixClientFallbackBusiness implements
      MerchantRechargeRecordBusinessFeignClient {

    private static Logger logger = LoggerFactory.getLogger(MerchantUserAccountFeignClient.class);


    private JSONResult fallBackError(String name) {
      logger.error(name + "接口调用失败：无法获取目标服务");
      return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
          SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
    }

    @Override
    public JSONResult<RechargeAccountDTO> getRechargeMoney(@RequestBody
        MerchantRechargeRecordQueryDTO queryDTO) {
      return fallBackError("获取商家累计充值总金额、累计赠送总金额、剩余金额");
    }

    @Override
    public JSONResult<PageBean<MerchantRechargeRecordDTO>> queryBusinessPageList(@RequestBody
        MerchantRechargeRecordQueryDTO queryDTO) {
      return fallBackError("商家端充值记录列表数据查询");
    }

    @Override
    public JSONResult<Boolean> applyInvoice(MerchantApplyInvoiceReq req) {
      return fallBackError("申请发票");
    }

    @Override
    public JSONResult<MerchantRechargeResp> getWeChatAndAlipayCode(MerchantRechargeReq req) {
      return fallBackError("获取微信或支付宝的支付URL");
    }

    @Override
    public JSONResult<MerchantRechargeRecordDTO> getMerchantRechargeRecordInfo(
        MerchantRechargeReq req) {
      return fallBackError("查看充值详情");
    }
  }
}
