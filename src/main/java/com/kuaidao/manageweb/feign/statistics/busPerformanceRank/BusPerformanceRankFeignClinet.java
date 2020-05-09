package com.kuaidao.manageweb.feign.statistics.busPerformanceRank;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.stastics.dto.busPerformanceRank.BusPerformanceRankDto;
import com.kuaidao.stastics.dto.busPerformanceRank.BusPerformanceRankQueryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 商务报表-业绩报表-业绩排名
 */
@FeignClient(name = "statstics-service", path = "/statstics/busPerformanceRank",
        fallback = BusPerformanceRankFeignClinet.HystrixClientFallback.class)
public interface BusPerformanceRankFeignClinet {

    /**
     * 全部
     */
    @PostMapping("/getBusPerformanceRankList")
    JSONResult<List<BusPerformanceRankDto>> getBusPerformanceRankList(@RequestBody BusPerformanceRankQueryDto busPerformanceRankQueryDto);

    /**
     * 分页
     */
    @PostMapping("/getBusPerformanceRankPage")
    JSONResult<PageBean<BusPerformanceRankDto>> getBusPerformanceRankPage(@RequestBody BusPerformanceRankQueryDto busPerformanceRankQueryDto);

    @Component
    class HystrixClientFallback implements BusPerformanceRankFeignClinet {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<List<BusPerformanceRankDto>> getBusPerformanceRankList(BusPerformanceRankQueryDto busPerformanceRankQueryDto) {
            return fallBackError("商务报表-业绩报表-业绩排名(全部)失败");
        }

        @Override
        public JSONResult<PageBean<BusPerformanceRankDto>> getBusPerformanceRankPage(BusPerformanceRankQueryDto busPerformanceRankQueryDto) {
            return fallBackError("商务报表-业绩报表-业绩排名(全部)失败");
        }
    }
}
