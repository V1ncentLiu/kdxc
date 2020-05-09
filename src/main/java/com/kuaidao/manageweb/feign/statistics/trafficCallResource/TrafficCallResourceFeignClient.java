package com.kuaidao.manageweb.feign.statistics.trafficCallResource;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.stastics.dto.trafficCallResource.TrafficCallResourceQueryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "statstics-service", path = "/statstics/trafficCallResource", fallback = TrafficCallResourceFeignClient.HystrixClientFallback.class)
public interface TrafficCallResourceFeignClient {


    /**
     * 一级页面查询组（不分页）
     */
    @PostMapping("/getGroupAllList")
    public JSONResult<Map<String,Object>> getGroupAllList(@RequestBody TrafficCallResourceQueryDto trafficCallResourceQueryDto);
    /**
     * 一级页面查询组（分页）
     */
    @PostMapping("/getGroupPageList")
    public JSONResult<Map<String,Object>> getGroupPageList(@RequestBody TrafficCallResourceQueryDto trafficCallResourceQueryDto);
    /**
     * 二级页面查询(不分页)
     */
    @PostMapping("/getPersonAllList")
    public JSONResult<Map<String,Object>> getPersonAllList(@RequestBody TrafficCallResourceQueryDto trafficCallResourceQueryDto);
    /**
     * 二级页面查询(分页)
     */
    @PostMapping("/getPersonPageList")
    public JSONResult<Map<String,Object>> getPersonPageList(@RequestBody TrafficCallResourceQueryDto trafficCallResourceQueryDto);
    /**
     * 三级页面查询(不分页)
     */
    @PostMapping("/getPersonDayAllList")
    public JSONResult<Map<String,Object>> getPersonDayAllList(@RequestBody TrafficCallResourceQueryDto trafficCallResourceQueryDto);
    /**
     * 三级页面查询(分页)
     */
    @PostMapping("/getPersonDayPageList")
    public JSONResult<Map<String,Object>> getPersonDayPageList(@RequestBody TrafficCallResourceQueryDto trafficCallResourceQueryDto);


    @Component
    class HystrixClientFallback implements TrafficCallResourceFeignClient {

        private static Logger logger = LoggerFactory.getLogger(TrafficCallResourceFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Map<String, Object>> getGroupAllList(TrafficCallResourceQueryDto trafficCallResourceQueryDto) {
            return fallBackError("话务资源分配处理明细组查询");
        }

        @Override
        public JSONResult<Map<String, Object>> getGroupPageList(TrafficCallResourceQueryDto trafficCallResourceQueryDto) {
            return fallBackError("话务资源分配处理明细组分页查询");
        }

        @Override
        public JSONResult<Map<String, Object>> getPersonAllList(TrafficCallResourceQueryDto trafficCallResourceQueryDto) {
            return fallBackError("话务资源分配处理明细个人查询");
        }

        @Override
        public JSONResult<Map<String, Object>> getPersonPageList(TrafficCallResourceQueryDto trafficCallResourceQueryDto) {
            return fallBackError("话务资源分配处理明细个人分页查询");
        }

        @Override
        public JSONResult<Map<String, Object>> getPersonDayAllList(TrafficCallResourceQueryDto trafficCallResourceQueryDto) {
            return fallBackError("话务资源分配处理明细天查询");
        }

        @Override
        public JSONResult<Map<String, Object>> getPersonDayPageList(TrafficCallResourceQueryDto trafficCallResourceQueryDto) {
            return fallBackError("话务资源分配处理明细天分页查询");
        }
    }

}
