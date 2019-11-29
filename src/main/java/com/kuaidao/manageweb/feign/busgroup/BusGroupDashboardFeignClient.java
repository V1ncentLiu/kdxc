package com.kuaidao.manageweb.feign.busgroup;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.dashboard.dto.bussale.BusGroupDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "dashboard-service", path = "/dashboard/dashboardBusGroup", fallback = BusGroupDashboardFeignClient.HystrixClientFallback.class)
public interface BusGroupDashboardFeignClient {

    /**
     * 查询商务总监看板数据
     */
    @PostMapping("/busGroupDataQuery")
    JSONResult<BusGroupDTO> busGroupDataQuery(@RequestParam Map map);


    @Component
    static class HystrixClientFallback implements BusGroupDashboardFeignClient {

        private static Logger logger = LoggerFactory.getLogger(BusGroupDashboardFeignClient.class);
        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }
        @Override
        public JSONResult<BusGroupDTO> busGroupDataQuery(Map map) {
            return fallBackError("商务总监看板计数");
        }
    }
}
