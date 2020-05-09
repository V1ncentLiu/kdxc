package com.kuaidao.manageweb.feign.statistics.trafficCallTime;


import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.stastics.dto.trafficCallTime.TrafficCallTimeQueryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "statstics-service", path = "/statstics/trafficCallTime", fallback = TrafficCallTimeFeignClient.HystrixClientFallback.class)
public interface TrafficCallTimeFeignClient {

    /**
     * 组分页
     */
    @PostMapping("/getGroupPageList")
    JSONResult<Map<String,Object>> getGroupPageList(@RequestBody TrafficCallTimeQueryDto trafficCallTimeQueryDto);

    /**
     * 组不分页
     */
    @PostMapping("/getGroupList")
    JSONResult<Map<String,Object>> getGroupList(@RequestBody TrafficCallTimeQueryDto trafficCallTimeQueryDto);


    /**
     * 组+人 分页
     */
    @PostMapping("/getGroupPersonPageList")
    JSONResult<Map<String,Object>> getGroupPersonPageList(@RequestBody TrafficCallTimeQueryDto trafficCallTimeQueryDto);

    /**
     * 组+人 不分页
     */
    @PostMapping("/getGroupPersonList")
    JSONResult<Map<String,Object>> getGroupPersonList(@RequestBody TrafficCallTimeQueryDto trafficCallTimeQueryDto);

    /**
     * 组+人+天 分页
     */
    @PostMapping("/getGroupPersonDayPageList")
    JSONResult<Map<String,Object>> getGroupPersonDayPageList(@RequestBody TrafficCallTimeQueryDto trafficCallTimeQueryDto);

    /**
     * 组+人+天 不分页
     */
    @PostMapping("/getGroupPersonDayList")
    JSONResult<Map<String,Object>> getGroupPersonDayList(@RequestBody TrafficCallTimeQueryDto trafficCallTimeQueryDto);


    @Component
    class HystrixClientFallback implements TrafficCallTimeFeignClient {

        private static Logger logger = LoggerFactory.getLogger(TrafficCallTimeFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Map<String, Object>> getGroupPageList(TrafficCallTimeQueryDto trafficCallTimeQueryDto) {
            return fallBackError("话务通话时长组级别分页查询");
        }

        @Override
        public JSONResult<Map<String, Object>> getGroupList(TrafficCallTimeQueryDto trafficCallTimeQueryDto) {
            return fallBackError("话务通话时长组级别查询");
        }

        @Override
        public JSONResult<Map<String, Object>> getGroupPersonPageList(TrafficCallTimeQueryDto trafficCallTimeQueryDto) {
            return fallBackError("话务通话时长组+人级别分页查询");
        }

        @Override
        public JSONResult<Map<String, Object>> getGroupPersonList(TrafficCallTimeQueryDto trafficCallTimeQueryDto) {
            return fallBackError("话务通话时长组+人级别查询");
        }

        @Override
        public JSONResult<Map<String, Object>> getGroupPersonDayPageList(TrafficCallTimeQueryDto trafficCallTimeQueryDto) {
            return fallBackError("话务通话时长组+人+日期级别分页查询");
        }

        @Override
        public JSONResult<Map<String, Object>> getGroupPersonDayList(TrafficCallTimeQueryDto trafficCallTimeQueryDto) {
            return fallBackError("话务通话时长组+人+日期级别查询");
        }
    }
}
