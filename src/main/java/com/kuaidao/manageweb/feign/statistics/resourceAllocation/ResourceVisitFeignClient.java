package com.kuaidao.manageweb.feign.statistics.resourceAllocation;

import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.stastics.dto.base.BaseQueryDto;
import com.kuaidao.stastics.dto.performance.PerformanceDto;
import com.kuaidao.stastics.dto.resourceAllocation.ResourceVisitDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@FeignClient(name="statstics-service",path = "/statstics/resourceVisit", fallback =ResourceVisitFeignClient.HystrixClientFallback.class )
public interface ResourceVisitFeignClient {

    @RequestMapping("/queryPage")
    public JSONResult<Map<String,Object>> queryByPage(@RequestBody BaseQueryDto baseQueryDto);

    @RequestMapping("/queryList")
    public JSONResult<List<ResourceVisitDto>> queryListByParams(@RequestBody BaseQueryDto baseQueryDto);

    @RequestMapping("/queryManagerPage")
    public JSONResult<Map<String,Object>> queryManagerPage(@RequestBody BaseQueryDto baseQueryDto);

    @RequestMapping("/queryManagerList")
    public JSONResult<List<ResourceVisitDto>> queryManagerList(@RequestBody BaseQueryDto baseQueryDto);

    @RequestMapping("/querySalePage")
    public JSONResult<Map<String,Object>> querySalePage(@RequestBody BaseQueryDto baseQueryDto);


    @RequestMapping("/querySaleList")
    public JSONResult<List<ResourceVisitDto>> querySaleList(@RequestBody BaseQueryDto baseQueryDto);


    @Component
    class HystrixClientFallback implements ResourceVisitFeignClient {

        @Override
        public JSONResult<Map<String, Object>> queryByPage(BaseQueryDto baseQueryDto) {
            return null;
        }

        @Override
        public JSONResult<List<ResourceVisitDto>> queryListByParams(BaseQueryDto baseQueryDto) {
            return null;
        }

        @Override
        public JSONResult<Map<String, Object>> queryManagerPage(BaseQueryDto baseQueryDto) {
            return null;
        }

        @Override
        public JSONResult<List<ResourceVisitDto>> queryManagerList(BaseQueryDto baseQueryDto) {
            return null;
        }

        @Override
        public JSONResult<Map<String, Object>> querySalePage(BaseQueryDto baseQueryDto) {
            return null;
        }

        @Override
        public JSONResult<List<ResourceVisitDto>> querySaleList(BaseQueryDto baseQueryDto) {
            return null;
        }
    }
}
