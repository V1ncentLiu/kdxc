package com.kuaidao.manageweb.feign.statistics;


import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.stastics.dto.ResourceAllocationDto;
import com.kuaidao.stastics.dto.ResourceAllocationQueryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "statstics-service", path = "/statstics/resourceAllocation", fallback = StatisticsFeignClient.HystrixClientFallback.class)
public interface StatisticsFeignClient {

    @PostMapping("/getResourceAllocationList")
    JSONResult<List<ResourceAllocationDto>> getResourceAllocationList(@RequestBody ResourceAllocationQueryDto resourceAllocationQueryDto);

    @PostMapping("/getResourceAllocationPage")
    JSONResult<PageBean<ResourceAllocationDto>> getResourceAllocationPage(@RequestBody ResourceAllocationQueryDto resourceAllocationQueryDto);

    @Component
    class HystrixClientFallback implements StatisticsFeignClient {

        private static Logger logger = LoggerFactory.getLogger(StatisticsFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<List<ResourceAllocationDto>> getResourceAllocationList(ResourceAllocationQueryDto resourceAllocationQueryDto) {
            return fallBackError("查询分配列表失败");
        }

        @Override
        public JSONResult<PageBean<ResourceAllocationDto>> getResourceAllocationPage(ResourceAllocationQueryDto resourceAllocationQueryDto) {
            return fallBackError("分页查询分配列表失败");
        }
    }
}
