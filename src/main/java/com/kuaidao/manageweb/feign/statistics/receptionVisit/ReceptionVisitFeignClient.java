package com.kuaidao.manageweb.feign.statistics.receptionVisit;


import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.stastics.dto.receptionVisit.ReceptionVisitQueryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "statstics-service", path = "/statstics/receptionVisit", fallback = ReceptionVisitFeignClient.HystrixClientFallback.class)
public interface ReceptionVisitFeignClient {

    /**
     *
     * 一级页面查询人（不分页）
     */
    @PostMapping("/getPersonAllList")
    JSONResult<Map<String,Object>> getPersonAllList(@RequestBody ReceptionVisitQueryDto receptionVisitQueryDto);
    /**
     *
     * 一级页面查询人（分页）
     */
    @PostMapping("/getPersonPageList")
    JSONResult<Map<String,Object>> getPersonPageList(@RequestBody ReceptionVisitQueryDto receptionVisitQueryDto);

    /**
     *
     * 二级页面查询人+天（不分页）
     */
    @PostMapping("/getPersonDayAllList")
    JSONResult<Map<String,Object>> getPersonDayAllList(@RequestBody ReceptionVisitQueryDto receptionVisitQueryDto);
    /**
     *
     * 二级页面查询人+天（分页）
     */
    @PostMapping("/getPersonDayPageList")
    JSONResult<Map<String,Object>> getPersonDayPageList(@RequestBody ReceptionVisitQueryDto receptionVisitQueryDto);

    @Component
    class HystrixClientFallback implements ReceptionVisitFeignClient {

        private static Logger logger = LoggerFactory.getLogger(ReceptionVisitFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Map<String, Object>> getPersonAllList(ReceptionVisitQueryDto receptionVisitQueryDto) {
            return fallBackError("商务来访接待工作表人全部查询");
        }

        @Override
        public JSONResult<Map<String, Object>> getPersonPageList(ReceptionVisitQueryDto receptionVisitQueryDto) {
            return fallBackError("商务来访接待工作表人分页查询");
        }

        @Override
        public JSONResult<Map<String, Object>> getPersonDayAllList(ReceptionVisitQueryDto receptionVisitQueryDto) {
            return fallBackError("商务来访接待工作表人+天全部查询");
        }

        @Override
        public JSONResult<Map<String, Object>> getPersonDayPageList(ReceptionVisitQueryDto receptionVisitQueryDto) {
            return fallBackError("商务来访接待工作表人+天分页查询");
        }
    }
}
