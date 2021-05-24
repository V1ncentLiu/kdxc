package com.kuaidao.manageweb.feign.merchant.recharge;

import com.kuaidao.account.dto.recharge.MerchantRechargeRecordDTO;
import com.kuaidao.account.dto.recharge.MerchantRechargeRecordQueryDTO;
import com.kuaidao.account.dto.recharge.MerchantRechargeReq;
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
@FeignClient(name = "account-service-3", path = "/account/merchantRechargeManageRecord",
    fallback = MerchantRechargeRecordManageFeignClient.HystrixClientFallbackBusiness.class)
public interface MerchantRechargeRecordManageFeignClient {

  /**
   * @Description 获取管理人员当日和当月充值总金额
   * @param queryDTO
   * @Return com.kuaidao.common.entity.JSONResult<com.kuaidao.account.dto.recharge.MerchantRechargeRecordDTO>
   * @Author xuyunfeng
   * @Date 2019/9/25 20:32
   **/
  @PostMapping("/getNowDayAndMonthRechargeMoney")
  public JSONResult<RechargeAccountDTO> getNowDayAndMonthRechargeMoney(@RequestBody
      MerchantRechargeRecordQueryDTO queryDTO);

  /**
   * @Description 管理端端充值记录列表数据查询
   * @param queryDTO
   * @Return com.kuaidao.common.entity.JSONResult<com.kuaidao.account.dto.recharge.MerchantRechargeRecordDTO>
   * @Author xuyunfeng
   * @Date 2019/9/26 11:39
   **/
  @PostMapping("/queryManagePageList")
  public JSONResult<PageBean<MerchantRechargeRecordDTO>> queryManagePageList(
      @RequestBody MerchantRechargeRecordQueryDTO queryDTO);

  /**
   * @Description 录入线下付款信息
   * @param
   * @Return com.kuaidao.common.entity.JSONResult<java.lang.Boolean>
   * @Author xuyunfeng
   * @Date 2019/9/27 15:30
   **/
  @RequestMapping("/saveOfflinePayment")
  public JSONResult<Boolean> saveOfflinePayment(@RequestBody MerchantRechargeReq req);

  @Component
  static class HystrixClientFallbackBusiness implements
      MerchantRechargeRecordManageFeignClient {

    private static Logger logger = LoggerFactory.getLogger(MerchantUserAccountFeignClient.class);


    private JSONResult fallBackError(String name) {
      logger.error(name + "接口调用失败：无法获取目标服务");
      return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
          SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
    }

    @Override
    public JSONResult<RechargeAccountDTO> getNowDayAndMonthRechargeMoney(@RequestBody
        MerchantRechargeRecordQueryDTO queryDTO) {
      return fallBackError("获取管理人员当日和当月充值总金额");
    }

    @Override
    public JSONResult<PageBean<MerchantRechargeRecordDTO>> queryManagePageList(@RequestBody
        MerchantRechargeRecordQueryDTO queryDTO) {
      return fallBackError("录入线下付款信息");
    }

    @Override
    public JSONResult<Boolean> saveOfflinePayment(MerchantRechargeReq req) {
      return fallBackError("获取管理人员当日和当月充值总金额");
    }
  }
}
