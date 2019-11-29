package com.kuaidao.manageweb.feign.merchant.tracking;

import java.util.List;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.merchant.dto.tracking.TrackingInsertOrUpdateDTO;
import com.kuaidao.merchant.dto.tracking.TrackingReqDTO;
import com.kuaidao.merchant.dto.tracking.TrackingRespDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;


/**
 *
 * 功能描述: 资源跟进记录
 * 
 * @author fanjd
 * @date: 2019/10/10 17:35
 */
@FeignClient(name = "merchant-service", path = "/merchant/tracking", fallback = TrackingMerchantFeignClient.HystrixClientFallback.class)
public interface TrackingMerchantFeignClient {

    @RequestMapping("/insert")
    JSONResult<Boolean> saveTracking(@RequestBody TrackingInsertOrUpdateDTO dto);

    @RequestMapping("/update")
    JSONResult<Boolean> updateTracking(@RequestBody TrackingInsertOrUpdateDTO dto);

    @RequestMapping("/queryList")
    JSONResult<List<TrackingRespDTO>> queryList(@RequestBody TrackingReqDTO dto);

    @RequestMapping("/findByClueId")
    JSONResult<List<TrackingRespDTO>> findByClueId(@RequestBody IdListLongReq reqDto);

    @Component
    class HystrixClientFallback implements TrackingMerchantFeignClient {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);


        @SuppressWarnings("rawtypes")
        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(), SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult<Boolean> saveTracking(TrackingInsertOrUpdateDTO dto) {
            return fallBackError("资源跟进记录-新增");
        }

        @Override
        public JSONResult<Boolean> updateTracking(TrackingInsertOrUpdateDTO dto) {
            return fallBackError("资源跟进记录-更新");
        }

        @Override
        public JSONResult<List<TrackingRespDTO>> queryList(TrackingReqDTO dto) {
            return fallBackError("资源跟进记录-查询（不分页）");
        }

        @Override
        public JSONResult<List<TrackingRespDTO>> findByClueId( IdListLongReq reqDto) {
            return fallBackError("根据线索id查询跟进记录-查询）");
        }

    }

}
