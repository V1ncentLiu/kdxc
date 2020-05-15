package com.kuaidao.manageweb.feign.callcenter;

import com.kuaidao.callcenter.dto.merchantClient.MerchantOutBoundCallReq;
import com.kuaidao.callcenter.dto.merchantClient.MerchantOutBoundCallRespDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created on: 2019-10-10-22:39
 */
@FeignClient(name = "callcenter-service", path = "/callcenter/merchantClient", fallback = MerchantClientFeignClient.HystrixClientFallback.class)
public interface MerchantClientFeignClient {

    @PostMapping("/merchantOutboundCall")
    public JSONResult<MerchantOutBoundCallRespDTO> merchantOutboundCall(
        @RequestBody MerchantOutBoundCallReq callDTO);

    @Component
    static class HystrixClientFallback implements MerchantClientFeignClient {

        private static Logger logger = LoggerFactory.getLogger(
            MerchantClientFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<MerchantOutBoundCallRespDTO> merchantOutboundCall(
            MerchantOutBoundCallReq callDTO) {
            return fallBackError("商家外呼");
        }
    }

}
