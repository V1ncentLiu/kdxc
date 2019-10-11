package com.kuaidao.manageweb.feign.merchant.bussinesscall;

import com.kuaidao.account.dto.call.MerchantCallCostDTO;
import com.kuaidao.account.dto.call.MerchantCallCostReq;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

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
  public JSONResult<PageBean<MerchantCallCostDTO>> getBussinessCallCostList(MerchantCallCostReq req);
  /**
   * @Description 管理端商家通话费用查询
   * @param req
   * @Return com.kuaidao.common.entity.JSONResult<com.kuaidao.common.entity.PageBean<com.kuaidao.account.dto.call.MerchantCallCostDTO>>
   * @Author xuyunfeng
   * @Date 2019/10/10 17:47
   **/
  @RequestMapping("/getManageCallCostList")
  public JSONResult<PageBean<MerchantCallCostDTO>> getManageCallCostList(MerchantCallCostReq req);


  @Component
  class HystrixClientFallback implements FallbackFactory<MerchantCallCostFeign> {

    private static Logger logger = LoggerFactory
        .getLogger(CallPackageFeignClient.HystrixClientFallback.class);

    @Override
    public MerchantCallCostFeign create(Throwable cause) {
      return new MerchantCallCostFeign() {

        @Override
        public JSONResult<PageBean<MerchantCallCostDTO>> getBussinessCallCostList(
            MerchantCallCostReq req) {
          return fallBackError("商家端商家通话费用列表查询");
        }

        @Override
        public JSONResult<PageBean<MerchantCallCostDTO>> getManageCallCostList(
            MerchantCallCostReq req) {
          return fallBackError("管理端商家通话费用查询");
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
