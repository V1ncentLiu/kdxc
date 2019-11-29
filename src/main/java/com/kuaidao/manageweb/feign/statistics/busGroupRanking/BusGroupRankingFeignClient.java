package com.kuaidao.manageweb.feign.statistics.busGroupRanking;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.feign.statistics.appointmentVisit.AppointmentVisitFeignClient;
import com.kuaidao.stastics.dto.base.BaseBusQueryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * 集团项目业绩表
 */
@FeignClient(name = "statstics-service", path = "/statstics/busGroupRanking", fallback = BusGroupRankingFeignClient.HystrixClientFallback.class)
public interface BusGroupRankingFeignClient {


    /**
     * 一级页面分页查询
     */
    @RequestMapping("/getOnePageList")
    JSONResult<Map<String,Object>> getOneBusGroupRankingPageList(@RequestBody BaseBusQueryDto baseBusQueryDto);
    /**
     * 一级页面查询全部
     */
    @RequestMapping("/getOneList")
    JSONResult<Map<String,Object>> getOneBusGroupRankingList(@RequestBody BaseBusQueryDto baseBusQueryDto);
    /**
     * 二级页面分页查询
     */
    @RequestMapping("/getTwoPageList")
    JSONResult<Map<String,Object>> getTwoBusGroupRankingPageList(@RequestBody BaseBusQueryDto baseBusQueryDto);
    /**
     * 二级页面查询全部
     */
    @RequestMapping("/getTwoList")
    JSONResult<Map<String,Object>> getTwoBusGroupRankingList(@RequestBody BaseBusQueryDto baseBusQueryDto);


    @Component
    class HystrixClientFallback implements BusGroupRankingFeignClient {
        private static Logger logger = LoggerFactory.getLogger(AppointmentVisitFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Map<String, Object>> getOneBusGroupRankingPageList(BaseBusQueryDto baseBusQueryDto) {
            return fallBackError("集团项目业绩表分页查询一级页面");
        }

        @Override
        public JSONResult<Map<String, Object>> getOneBusGroupRankingList(BaseBusQueryDto baseBusQueryDto) {
            return fallBackError("集团项目业绩表查询全部一级页面");
        }

        @Override
        public JSONResult<Map<String, Object>> getTwoBusGroupRankingPageList(BaseBusQueryDto baseBusQueryDto) {
            return fallBackError("集团项目业绩表分页查询二级页面");
        }

        @Override
        public JSONResult<Map<String, Object>> getTwoBusGroupRankingList(BaseBusQueryDto baseBusQueryDto) {
            return fallBackError("集团项目业绩表查询全部二级页面");
        }
    }

}
