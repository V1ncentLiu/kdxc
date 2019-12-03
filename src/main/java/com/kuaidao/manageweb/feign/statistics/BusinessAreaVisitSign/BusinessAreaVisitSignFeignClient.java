package com.kuaidao.manageweb.feign.statistics.BusinessAreaVisitSign;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.feign.statistics.appointmentVisit.AppointmentVisitFeignClient;
import com.kuaidao.stastics.dto.base.BaseBusQueryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * 集团项目业绩表
 */
@FeignClient(name = "statstics-service", path = "/statstics/businessAreaBisitSign", fallback = BusinessAreaVisitSignFeignClient.HystrixClientFallback.class)
public interface BusinessAreaVisitSignFeignClient {


    /**
     * 一级页面分页查询
     */
    @RequestMapping("/getBusinessAreaSignList")
    JSONResult<Map<String,Object>> getBusinessAreaSignList(@RequestBody BaseBusQueryDto baseBusQueryDto);


    @Component
    class HystrixClientFallback implements BusinessAreaVisitSignFeignClient {
        private static Logger logger = LoggerFactory.getLogger(AppointmentVisitFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Map<String, Object>> getBusinessAreaSignList(BaseBusQueryDto baseBusQueryDto) {
            return fallBackError("商务大区来访签约业绩表");
        }

    }

}
