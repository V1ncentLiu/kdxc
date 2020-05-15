package com.kuaidao.manageweb.feign.merchant.bussinesscall;

import com.kuaidao.account.dto.call.MerchantCallCostReq;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @description: MerchantCallCostFeign
 * @date: 2019/10/11 9:12
 * @author: xuyunfeng
 * @version: 1.0
 */
@FeignClient(name = "account-service", path = "/account/merchantCallCost",
    fallbackFactory = MerchantCallCostFeign.HystrixClientFallback.class)
public interface MerchantCallCostFeign {

  /**
   * @Description 商家端商家通话费用列表查询
   * @param req
   * @Return com.kuaidao.common.entity.JSONResult<com.kuaidao.common.entity.PageBean<com.kuaidao.account.dto.call.MerchantCallCostDTO>>
   * @Author xuyunfeng
   * @Date 2019/10/10 17:08
   **/
  @RequestMapping("/getBussinessCallCostList")
  public JSONResult<Map<String, Object>> getBussinessCallCostList(MerchantCallCostReq req);
  /**
   * @Description 管理端商家通话费用查询
   * @param req
   * @Return com.kuaidao.common.entity.JSONResult<com.kuaidao.common.entity.PageBean<com.kuaidao.account.dto.call.MerchantCallCostDTO>>
   * @Author xuyunfeng
   * @Date 2019/10/10 17:47
   **/
  @RequestMapping("/getManageCallCostList")
  public JSONResult<Map<String, Object>> getManageCallCostList(MerchantCallCostReq req);

  /**
   * @Description 获取商家累计消费
   * @param req
   * @Return com.kuaidao.common.entity.JSONResult<java.lang.String>
   * @Author xuyunfeng
   * @Date 2019/10/11 14:57
   **/
  @RequestMapping("/getTotalMerchantCost")
  public JSONResult<String> getTotalMerchantCost(MerchantCallCostReq req);

  @Component
  class HystrixClientFallback implements FallbackFactory<MerchantCallCostFeign> {

    private static Logger logger = LoggerFactory
        .getLogger(CallPackageFeignClient.HystrixClientFallback.class);

    @Override
    public MerchantCallCostFeign create(Throwable cause) {
      return new MerchantCallCostFeign() {

        @Override
        public JSONResult<Map<String, Object>> getBussinessCallCostList(@RequestBody MerchantCallCostReq req) {
          return fallBackError("商家端商家通话费用列表查询");
        }

        @Override
        public JSONResult<Map<String, Object>> getManageCallCostList(
            MerchantCallCostReq req) {
          return fallBackError("管理端商家通话费用查询");
        }

        @Override
        public JSONResult<String> getTotalMerchantCost(@RequestBody MerchantCallCostReq req) {
          return fallBackError("获取商家累计消费");
        }

        @SuppressWarnings("rawtypes")
        private JSONResult fallBackError(String name) {
          logger.error("接口调用失败");
          logger.error("接口名{}", name);
          logger.error("失败原因{}", cause);
          return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
              SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }
      };
    }


  }
}
