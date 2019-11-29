package com.kuaidao.manageweb.feign.statistics.busPerformance;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.stastics.dto.base.BaseBusQueryDto;
import com.kuaidao.stastics.dto.busPerformance.BusPerformanceDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;

@FeignClient(name = "statstics-service", path = "/statstics/busPerformance/", fallback = BusPerformanceClient.HystrixClientFallback.class)
public interface BusPerformanceClient {

    @PostMapping("/queryPage")
    public JSONResult<Map<String,Object>> queryByPage(BaseBusQueryDto dto);

    @PostMapping("/queryGroupList")
    public JSONResult<List<BusPerformanceDto>> queryList(BaseBusQueryDto dto);

    @PostMapping("/queryBusPage")
    public JSONResult<Map<String,Object>> queryBusPage(BaseBusQueryDto dto);

    @PostMapping("/queryBusList")
    public JSONResult<List<BusPerformanceDto>> queryBusList(BaseBusQueryDto dto);


    @Component
    class HystrixClientFallback implements BusPerformanceClient{


        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Map<String, Object>> queryByPage(BaseBusQueryDto dto) {
            return fallBackError("商务业绩-组分页查询");
        }

        @Override
        public JSONResult<List<BusPerformanceDto>> queryList(BaseBusQueryDto dto) {
            return fallBackError("商务业绩-组查询");
        }

        @Override
        public JSONResult<Map<String, Object>> queryBusPage(BaseBusQueryDto dto) {
            return fallBackError("商务业绩-组/人分页查询");
        }

        @Override
        public JSONResult<List<BusPerformanceDto>> queryBusList(BaseBusQueryDto dto) {
            return fallBackError("商务业绩-组/人查询");
        }
    }
}
