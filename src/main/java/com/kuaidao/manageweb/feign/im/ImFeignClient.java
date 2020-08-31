package com.kuaidao.manageweb.feign.im;

import com.kuaidao.common.constant.SysErrorCodeEnum;
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

@FeignClient(name = "im-service", path = "/v1.0/message", fallback = ImFeignClient.HystrixClientFallback.class)
public interface ImFeignClient {

    @PostMapping(value = "/getChatRecordPage")
    JSONPageResult<List<MessageRecordData>> getChatRecordPage(@RequestBody MessageRecordPageReq messageRecordPageReq);

    @PostMapping(value = "/getChatRecordList")
    JSONPageResult<List<MessageRecordData>> getChatRecordList(MessageRecordExportSearchReq messageRecordExportSearchReq);

    @Component
    static class HystrixClientFallback implements ImFeignClient {

        private static Logger logger = LoggerFactory.getLogger(ImFeignClient.class);

        private JSONPageResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONPageResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONPageResult<List<MessageRecordData>> getChatRecordPage(MessageRecordPageReq messageRecordPageReq) {
            return fallBackError("历史纪录分页");
        }

        @Override
        public JSONPageResult<List<MessageRecordData>> getChatRecordList(MessageRecordExportSearchReq messageRecordExportSearchReq) {
            return fallBackError("历史纪录导出");
        }
    }


}
