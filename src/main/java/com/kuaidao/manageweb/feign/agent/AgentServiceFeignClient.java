package com.kuaidao.manageweb.feign.agent;

import com.kuaidao.agentservice.dto.call.CallRecordCountDTO;
import com.kuaidao.agentservice.dto.call.CallRecordReqDTO;
import com.kuaidao.agentservice.dto.call.CallRecordRespDTO;
import com.kuaidao.agentservice.dto.circulation.CirculationInsertOrUpdateDTO;
import com.kuaidao.agentservice.dto.tracking.AgentClueTrackingDTO;
import com.kuaidao.agentservice.dto.tracking.TrackingReqDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 功能描述: agentservice fegin
 * @author zxy
 * @date 2021/3/2 17:35
 */
@FeignClient(name = "agent-service", path = "/agentservice", fallbackFactory = AgentServiceFeignClient.HystrixClientFallback.class)
public interface AgentServiceFeignClient {

    /**
     * 插入流转记录
     * @param dto
     * @return
     */
    @PostMapping("/agent/circulation/saveCirculation")
    public JSONResult<Boolean> saveCirculation(@RequestBody CirculationInsertOrUpdateDTO dto);

    @PostMapping("/agent/tracking/queryList")
    public JSONResult<List<AgentClueTrackingDTO>> queryTrackList(@RequestBody TrackingReqDTO dto);

    @PostMapping("/call/callRecord/countCallRecordTotalByClueIdList")
    public JSONResult<List<CallRecordCountDTO>> countCallRecordTotalByClueIdList(@RequestBody CallRecordReqDTO myCallRecordReqDTO);

    @PostMapping("/call/callRecord/listAgentCallRecordByParamsNoPage")
    public JSONResult<List<CallRecordRespDTO>> listAgentCallRecordByParamsNoPage (@RequestBody CallRecordReqDTO myCallRecordReqDTO);

    @Component
    static class HystrixClientFallback implements FallbackFactory<AgentServiceFeignClient> {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        @Override
        public AgentServiceFeignClient create(Throwable cause) {
            return new AgentServiceFeignClient() {
                @SuppressWarnings("rawtypes")
                private JSONResult fallBackError(String name) {
                    logger.error("接口调用失败");
                    logger.error("接口名{}", name);
                    logger.error("失败原因{}", cause);
                    return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(), SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
                }

                @Override
                public JSONResult<Boolean> saveCirculation(@RequestBody CirculationInsertOrUpdateDTO dto) {
                    return fallBackError("插入流转记录");
                }

                @Override
                public JSONResult<List<AgentClueTrackingDTO>> queryTrackList(TrackingReqDTO dto) {
                    return fallBackError("查询跟进记录");
                }

                @Override
                public JSONResult<List<CallRecordCountDTO>> countCallRecordTotalByClueIdList(@RequestBody CallRecordReqDTO myCallRecordReqDTO) {
                    return fallBackError("查询通话记录");
                }

                @Override
                public JSONResult<List<CallRecordRespDTO>> listAgentCallRecordByParamsNoPage(CallRecordReqDTO myCallRecordReqDTO) {
                    return fallBackError("查询通话记录");
                }

            };
        }

    }

}
