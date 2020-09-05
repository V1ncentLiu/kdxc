package com.kuaidao.manageweb.feign.im;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.custservice.dto.submitCust.SubmitCustsDTO;
import com.kuaidao.im.dto.MessageRecordData;
import com.kuaidao.im.dto.MessageRecordExportSearchReq;
import com.kuaidao.im.dto.MessageRecordPageReq;
import com.kuaidao.im.util.JSONPageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "cust-service-service", path = "/custservice/submitCustomerRecord", fallback = ImSubmitCustomerRecordClient.HystrixClientFallback.class)
public interface ImSubmitCustomerRecordClient {

    @PostMapping(value = "/submit")
    JSONResult<Long> submit(@RequestBody SubmitCustsDTO submitCustsDTO);


    @Component
    static class HystrixClientFallback implements ImSubmitCustomerRecordClient {

        private static Logger logger = LoggerFactory.getLogger(ImSubmitCustomerRecordClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult<Long> submit(SubmitCustsDTO submitCustsDTO) {
            return fallBackError("客户提交接口");
        }
    }


}
