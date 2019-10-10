package com.kuaidao.manageweb.feign.merchant.mserviceshow;

import com.kuaidao.account.dto.mservice.MerchantServiceDTO;
import com.kuaidao.account.dto.mservice.MerchantServiceReq;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.merchant.publiccustomer.PubcustomerFeignClient.HystrixClientFallback;
import com.kuaidao.merchant.dto.clue.ResourceStatisticsDto;
import com.kuaidao.merchant.dto.clue.ResourceStatisticsParamDTO;
import com.kuaidao.merchant.dto.pubcusres.ClueQueryParamDTO;
import com.kuaidao.merchant.dto.pubcusres.ClueReceiveRecordsDTO;
import com.kuaidao.merchant.dto.pubcusres.PublicCustomerResourcesRespDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 *
 * 功能描述:
 *  服务展示列表
 * @author yangbiao
 * @date: 2019/1/8 17:35
 */
@FeignClient(name = "account-service",path="/account/mservice",fallback = HystrixClientFallback.class)
public interface MserviceShowFeignClient {


  @PostMapping("/queryList")
  public JSONResult<List<MerchantServiceDTO>> queryList(MerchantServiceReq merchantServiceReq);

    @Component
    static class HystrixClientFallback implements
        MserviceShowFeignClient {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        @SuppressWarnings("rawtypes")
        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


      @Override
      public JSONResult<List<MerchantServiceDTO>> queryList(MerchantServiceReq merchantServiceReq) {
        return fallBackError("商家服务套餐展示");
      }
    }

}
