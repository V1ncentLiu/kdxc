package com.kuaidao.manageweb.feign.statistics.busTeleDistribution;

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

@FeignClient(name = "statstics-service", path = "/statstics/busTeleDistribution/", fallback = BusTeleDistributionFeignClient.HystrixClientFallback.class)
public interface BusTeleDistributionFeignClient {


    /**
     *
     * 一级页面查询（不分页）
     */
    @RequestMapping("/getOneAllList")
    JSONResult<Map<String,Object>> getOneAllList(@RequestBody BaseBusQueryDto baseBusQueryDto);
    /**
     *
     * 一级页面查询（分页）
     */
    @RequestMapping("/getOnePageList")
    JSONResult<Map<String,Object>> getOnePageList(@RequestBody BaseBusQueryDto baseBusQueryDto);

    /**
     *
     * 二级页面查询（不分页）
     */
    @RequestMapping("/getTwoAllList")
    JSONResult<Map<String,Object>> getTwoAllList(@RequestBody BaseBusQueryDto baseBusQueryDto);
    /**
     *
     * 二级页面查询（分页）
     */
    @RequestMapping("/getTwoPageList")
    JSONResult<Map<String,Object>> getTwoPageList(@RequestBody BaseBusQueryDto baseBusQueryDto);


    @Component
    class HystrixClientFallback implements BusTeleDistributionFeignClient {
        private static Logger logger = LoggerFactory.getLogger(AppointmentVisitFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Map<String, Object>> getOneAllList(BaseBusQueryDto baseBusQueryDto) {
            return fallBackError("商务报表-业绩报表-电销组织分布表一级页面查询(全部)");
        }

        @Override
        public JSONResult<Map<String, Object>> getOnePageList(BaseBusQueryDto baseBusQueryDto) {
            return fallBackError("商务报表-业绩报表-电销组织分布表一级页面查询(分页)");
        }

        @Override
        public JSONResult<Map<String, Object>> getTwoAllList(BaseBusQueryDto baseBusQueryDto) {
            return fallBackError("商务报表-业绩报表-电销组织分布表二级页面查询(全部)");
        }

        @Override
        public JSONResult<Map<String, Object>> getTwoPageList(BaseBusQueryDto baseBusQueryDto) {
            return fallBackError("商务报表-业绩报表-电销组织分布表二级页面查询(分页)");
        }
    }
}
