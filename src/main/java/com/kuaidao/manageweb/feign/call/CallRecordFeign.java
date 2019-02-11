package com.kuaidao.manageweb.feign.call;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.aggregation.dto.call.CallRecordReqDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;


@FeignClient(name = "aggregation-service-chen", path = "/aggregation/call/callRecord", fallback = CallRecordFeign.HystrixClientFallback.class)
public interface CallRecordFeign {
    
    /**
     *   获取我的通话记录 分页展示 ，参数模糊匹配
     * @return
     */
    @PostMapping("/listMyCallRecord")
    JSONResult<Map<String,Object>> listMyCallRecord(@RequestBody CallRecordReqDTO myCallRecordReqDTO);
    
    /**
     * 电销通话记录  分页展示 ，参数模糊匹配
     * @return
     */
    @PostMapping("/listAllTmCallRecord")
    JSONResult<Map<String,Object>> listAllTmCallRecord(@RequestBody CallRecordReqDTO myCallRecordReqDTO);
    
    /***
     * 电销通话时长统计 分页
     * @param myCallRecordReqDTO
     * @return
     */
    @PostMapping("/listAllTmCallTalkTime")
    JSONResult<Map<String,Object>> listAllTmCallTalkTime(@RequestBody CallRecordReqDTO myCallRecordReqDTO);
    
    @Component
    static class HystrixClientFallback implements CallRecordFeign {
        
        private static Logger logger = LoggerFactory.getLogger(CallRecordFeign.class);
        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }
        @Override
        public JSONResult<Map<String, Object>> listMyCallRecord(
                CallRecordReqDTO myCallRecordReqDTO) {
            return fallBackError("我的通话记录");
        }
        @Override
        public JSONResult<Map<String, Object>> listAllTmCallRecord(
                CallRecordReqDTO myCallRecordReqDTO) {
            return fallBackError("电销通话记录");
        }
        @Override
        public JSONResult<Map<String, Object>> listAllTmCallTalkTime(
                CallRecordReqDTO myCallRecordReqDTO) {
            return fallBackError("电销通话时长");
        }
        
        
    }
}
