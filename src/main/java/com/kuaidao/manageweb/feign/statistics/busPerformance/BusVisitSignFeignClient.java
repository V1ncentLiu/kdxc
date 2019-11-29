package com.kuaidao.manageweb.feign.statistics.busPerformance;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.stastics.dto.base.BaseBusQueryDto;
import com.kuaidao.stastics.dto.busPerformance.BusVisitSignDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;

@FeignClient(name="statstics-service",path ="/statstics/busVisitSign/", fallback =BusVisitSignFeignClient.HystrixClientFallback.class)
public interface BusVisitSignFeignClient {



    @PostMapping("/queryPage")
    public JSONResult<Map<String,Object>> queryByPage(BaseBusQueryDto dto);

    @PostMapping("/queryList")
    public JSONResult<List<BusVisitSignDto>> queryList(BaseBusQueryDto dto);

    @PostMapping("/queryGroupPage")
    public JSONResult<Map<String,Object>> queryBusPage(BaseBusQueryDto dto);

    @PostMapping("/queryGroupList")
    public JSONResult<List<BusVisitSignDto>> queryBusList(BaseBusQueryDto dto);

    @PostMapping("/queryPageForSale")
    public JSONResult<Map<String,Object>> queryBusPageForSale(BaseBusQueryDto dto);

    @PostMapping("/queryListForSale")
    public JSONResult<List<BusVisitSignDto>> queryBusListSale(BaseBusQueryDto dto);

    @Component
    class HystrixClientFallback implements BusVisitSignFeignClient {

        private static Logger logger = LoggerFactory.getLogger(BusVisitSignFeignClient.HystrixClientFallback.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Map<String, Object>> queryByPage(BaseBusQueryDto dto) {
            return fallBackError("来访签约区域表分页查询");
        }

        @Override
        public JSONResult<List<BusVisitSignDto>> queryList(BaseBusQueryDto dto) {
            return fallBackError("来访签约区域表导出查询");
        }

        @Override
        public JSONResult<Map<String, Object>> queryBusPage(BaseBusQueryDto dto) {
            return fallBackError("集团来访签约区域表分页查询");
        }

        @Override
        public JSONResult<List<BusVisitSignDto>> queryBusList(BaseBusQueryDto dto) {
            return fallBackError("集团来访签约区域表导出查询");
        }

        @Override
        public JSONResult<Map<String, Object>> queryBusPageForSale(BaseBusQueryDto dto) {
            return fallBackError("总监查询集团来访签约区域-分页");
        }

        @Override
        public JSONResult<List<BusVisitSignDto>> queryBusListSale(BaseBusQueryDto dto) {
            return fallBackError("总监查询集团来访签约区域-导出");
        }
    }

}
