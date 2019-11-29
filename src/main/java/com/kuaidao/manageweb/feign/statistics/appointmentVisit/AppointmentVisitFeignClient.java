package com.kuaidao.manageweb.feign.statistics.appointmentVisit;


import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.stastics.dto.appointmentVisit.AppointmentVisitQueryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@FeignClient(name = "statstics-service", path = "/statstics/appointmentVisit", fallback = AppointmentVisitFeignClient.HystrixClientFallback.class)
public interface AppointmentVisitFeignClient {

    /**
     * 组分页
     */
    @RequestMapping("/getGroupPageList")
    JSONResult<Map<String,Object>> getGroupPageList(@RequestBody AppointmentVisitQueryDto appointmentVisitQueryDto);

    /**
     * 组不分页
     */
    @RequestMapping("/getGroupList")
    JSONResult<Map<String,Object>> getGroupList(@RequestBody AppointmentVisitQueryDto appointmentVisitQueryDto);

    /**
     * 组+天分页
     */
    @RequestMapping("/getGroupDayPageList")
    JSONResult<Map<String,Object>> getGroupDayPageList(@RequestBody AppointmentVisitQueryDto appointmentVisitQueryDto);

    /**
     * 组+天不分页
     */
    @RequestMapping("/getGroupDayList")
    JSONResult<Map<String,Object>> getGroupDayList(@RequestBody AppointmentVisitQueryDto appointmentVisitQueryDto);


    @Component
    class HystrixClientFallback implements AppointmentVisitFeignClient {
        private static Logger logger = LoggerFactory.getLogger(AppointmentVisitFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Map<String, Object>> getGroupPageList(AppointmentVisitQueryDto appointmentVisitQueryDto) {
            return fallBackError("预约来访查询组分页失败");
        }

        @Override
        public JSONResult<Map<String, Object>> getGroupList(AppointmentVisitQueryDto appointmentVisitQueryDto) {
            return fallBackError("预约来访查询组全部失败");
        }

        @Override
        public JSONResult<Map<String, Object>> getGroupDayPageList(AppointmentVisitQueryDto appointmentVisitQueryDto) {
            return fallBackError("预约来访查询组+天分页失败");
        }

        @Override
        public JSONResult<Map<String, Object>> getGroupDayList(AppointmentVisitQueryDto appointmentVisitQueryDto) {
            return fallBackError("预约来访查询组+天全部失败");
        }
    }

}
