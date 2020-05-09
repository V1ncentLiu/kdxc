package com.kuaidao.manageweb.feign.statistics.resourceEfficiency;


import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.stastics.dto.resourceEfficiency.ResourceEfficiencyQueryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "statstics-service", path = "/statstics/resourceEfficiency", fallback = ResourceEfficiencyFeignClient.HystrixClientFallback.class)
public interface ResourceEfficiencyFeignClient {

    @PostMapping("/getResourceEfficientPageList")
    JSONResult<Map<String,Object>> getResourceEfficientPageList(@RequestBody ResourceEfficiencyQueryDto resourceEfficiencyQueryDto);

    @PostMapping("/getFirstResourceEfficientPageList")
    JSONResult<Map<String,Object>> getFirstResourceEfficientPageList(@RequestBody ResourceEfficiencyQueryDto resourceEfficiencyQueryDto);

    @PostMapping("/getAllResourceEfficientList")
    JSONResult<Map<String,Object>> getAllResourceEfficientList(@RequestBody ResourceEfficiencyQueryDto resourceEfficiencyQueryDto);

    @Component
    class HystrixClientFallback implements ResourceEfficiencyFeignClient {

        private static Logger logger = LoggerFactory.getLogger(ResourceEfficiencyFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }
        @Override
        public JSONResult<Map<String,Object>> getResourceEfficientPageList(ResourceEfficiencyQueryDto resourceEfficiencyQueryDto) {
            return fallBackError("分页查询资源接通有效率失败");
        }

        @Override
        public JSONResult<Map<String, Object>> getFirstResourceEfficientPageList(ResourceEfficiencyQueryDto resourceEfficiencyQueryDto) {
            return fallBackError("分页查询首日资源接通有效率失败");
        }

        @Override
        public JSONResult<Map<String, Object>> getAllResourceEfficientList(ResourceEfficiencyQueryDto resourceEfficiencyQueryDto) {
            return fallBackError("查询全部资源接通有效率失败");
        }
    }
}
