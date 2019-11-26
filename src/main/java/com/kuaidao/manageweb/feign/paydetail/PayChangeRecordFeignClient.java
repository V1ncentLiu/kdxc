package com.kuaidao.manageweb.feign.paydetail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.aggregation.dto.paydetail.PayChangeRecordDTO;
import com.kuaidao.aggregation.dto.paydetail.PayChangeRecordParamDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;

@FeignClient(name = "aggregation-service", path = "/aggregation/payChangRecord", fallback = PayChangeRecordFeignClient.HystrixClientFallback.class)
public interface PayChangeRecordFeignClient {


    @PostMapping("/getPageList")
    JSONResult<PageBean<PayChangeRecordDTO>> getPageList(@RequestBody PayChangeRecordParamDTO payChangRecordParamDTO);


    @Component
    static class HystrixClientFallback implements PayChangeRecordFeignClient {
        private static Logger logger = LoggerFactory.getLogger(PayDetailFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(), SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<PageBean<PayChangeRecordDTO>> getPageList( PayChangeRecordParamDTO payChangRecordParamDTO) {
            return fallBackError("付款明细修改记录列表");
        }

    }
}
