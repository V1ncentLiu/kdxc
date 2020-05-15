package com.kuaidao.manageweb.feign.statistics.resourceAllocation;


import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.stastics.dto.resourceFree.ResourceFreeDto;
import com.kuaidao.stastics.dto.resourceFree.ResourceFreeQueryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "statstics-service", path = "/statstics/resourceFree", fallback = StatisticsFreeFeignClient.HystrixClientFallback.class)
public interface StatisticsFreeFeignClient {

    /**
     * 查询全部资源释放原因记录
     * @param resourceFreeQueryDto
     * @return
     */
    @PostMapping("/queryList")
    JSONResult<List<ResourceFreeDto>> queryList(@RequestBody ResourceFreeQueryDto resourceFreeQueryDto);



    @Component
    class HystrixClientFallback implements StatisticsFreeFeignClient {

        private static Logger logger = LoggerFactory.getLogger(StatisticsFreeFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<List<ResourceFreeDto>> queryList(ResourceFreeQueryDto resourceFreeQueryDto) {
            return fallBackError("查询资源释放原因失败");
        }
    }
}
