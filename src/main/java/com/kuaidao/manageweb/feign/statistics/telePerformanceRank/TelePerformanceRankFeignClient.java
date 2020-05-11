package com.kuaidao.manageweb.feign.statistics.telePerformanceRank;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.stastics.dto.telePerformanceRank.TelePerformanceRankDto;
import com.kuaidao.stastics.dto.telePerformanceRank.TelePerformanceRankQueryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


/**
 *  / 电销报表 / 业绩报表 / 业绩排名
 */
@FeignClient(name = "statstics-service", path = "/statstics/telePerformanceRank", fallback = TelePerformanceRankFeignClient.HystrixClientFallback.class)
public interface TelePerformanceRankFeignClient {

    /**
     * 全部
     */
    @PostMapping("/getPerformanceRankList")
    JSONResult<List<TelePerformanceRankDto>> getPerformanceRankList(@RequestBody TelePerformanceRankQueryDto telePerformanceRankQueryDto);

    /**
     * 分页
     */
    @PostMapping("/getPerformanceRankPage")
    JSONResult<PageBean<TelePerformanceRankDto>> getPerformanceRankPage(@RequestBody TelePerformanceRankQueryDto telePerformanceRankQueryDto);

    @Component
    class HystrixClientFallback implements TelePerformanceRankFeignClient {

        private static Logger logger = LoggerFactory.getLogger(TelePerformanceRankFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<List<TelePerformanceRankDto>> getPerformanceRankList(TelePerformanceRankQueryDto telePerformanceRankQueryDto) {
            return fallBackError("查询电销报表-业绩报表-业绩排名(全部)失败");
        }

        @Override
        public JSONResult<PageBean<TelePerformanceRankDto>> getPerformanceRankPage(TelePerformanceRankQueryDto telePerformanceRankQueryDto) {
            return fallBackError("查询电销报表-业绩报表-业绩排名(分页)失败");
        }
    }
}
