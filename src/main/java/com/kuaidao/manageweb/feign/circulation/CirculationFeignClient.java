package com.kuaidao.manageweb.feign.circulation;

import com.kuaidao.aggregation.dto.circulation.CirculationInsertOrUpdateDTO;
import com.kuaidao.aggregation.dto.circulation.CirculationReqDTO;
import com.kuaidao.aggregation.dto.circulation.CirculationRespDTO;
import com.kuaidao.aggregation.dto.tracking.TrackingInsertOrUpdateDTO;
import com.kuaidao.aggregation.dto.tracking.TrackingReqDTO;
import com.kuaidao.aggregation.dto.tracking.TrackingRespDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;


/**
 *
 * 功能描述: 
 *      资源流传记录
 * @auther  yangbiao
 * @date: 2019/1/8 17:35
 */
@FeignClient(name = "aggregation-service",path="/aggregation/circulation",fallback = CirculationFeignClient.HystrixClientFallback.class)
public interface CirculationFeignClient {

    @RequestMapping("/insert")
    public JSONResult<Boolean> saveCirculation(@RequestBody CirculationInsertOrUpdateDTO dto);

    @RequestMapping("/queryList")
    public JSONResult<List<CirculationRespDTO>> queryList(@RequestBody CirculationReqDTO dto);

    /**
     * 查询最早开始服务时间
     */
    @RequestMapping("/startTimeOfService")
    public JSONResult<CirculationRespDTO> startTimeOfService(@RequestBody CirculationReqDTO dto);

    @Component
    static class HystrixClientFallback implements CirculationFeignClient {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        @SuppressWarnings("rawtypes")
        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult<Boolean> saveCirculation(CirculationInsertOrUpdateDTO dto) {
            return fallBackError("流转人员记录-新增");
        }

        @Override
        public JSONResult<List<CirculationRespDTO>> queryList(CirculationReqDTO dto) {
            return fallBackError("流转人员记录-查询（不分页）");
        }

        @Override
        public JSONResult<CirculationRespDTO> startTimeOfService(CirculationReqDTO dto) {
            return fallBackError("最早开始服务时间");
        }
    }

}
