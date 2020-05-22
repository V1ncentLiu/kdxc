package com.kuaidao.manageweb.feign.visit;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.aggregation.dto.visit.TrackingOrderReqDTO;
import com.kuaidao.aggregation.dto.visit.TrackingOrderRespDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.client.ClientFeignClient;

@FeignClient(name = "aggregation-service-1", path = "/aggregation/visit/truckingOrder", fallback = TrackingOrderFeignClient.HystrixClientFallback.class)
public interface TrackingOrderFeignClient {
    
    
    @PostMapping("/listTrackingOrder")
    public JSONResult<PageBean<TrackingOrderRespDTO>> listTrackingOrder(@RequestBody TrackingOrderReqDTO reqDTO);
    
    @PostMapping("/exportTrackingOrder")
    public JSONResult<List<TrackingOrderRespDTO>> exportTrackingOrder(TrackingOrderReqDTO reqDTO);
    
    @Component
    static class HystrixClientFallback implements TrackingOrderFeignClient {

        private static Logger logger = LoggerFactory.getLogger(ClientFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<PageBean<TrackingOrderRespDTO>> listTrackingOrder(
                TrackingOrderReqDTO reqDTO) {
            return fallBackError("查询邀约来访派车单");
        }

        @Override
        public JSONResult<List<TrackingOrderRespDTO>> exportTrackingOrder(
                TrackingOrderReqDTO reqDTO) {
            return fallBackError("导出邀约来访派车单");
        }
    }

    
}
