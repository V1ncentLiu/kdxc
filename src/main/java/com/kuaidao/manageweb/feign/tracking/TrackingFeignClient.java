package com.kuaidao.manageweb.feign.tracking;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;


/**
 *
 * 功能描述: 
 *      资源跟进记录
 * @auther  yangbiao
 * @date: 2019/1/8 17:35
 */
@FeignClient(name = "aggregation-service-1",path="/aggregation/tracking",fallback = TrackingFeignClient.HystrixClientFallback.class)
public interface TrackingFeignClient {

    @RequestMapping("/insert")
    public JSONResult<Boolean> saveTracking(@RequestBody TrackingInsertOrUpdateDTO dto);

    @RequestMapping("/update")
    public JSONResult<Boolean>  updateTracking(@RequestBody TrackingInsertOrUpdateDTO dto);

    @RequestMapping("/delete")
    public JSONResult<Boolean>  deleteTracking(@RequestBody IdListLongReq idListReq);

    @RequestMapping("/queryList")
    public JSONResult<List<TrackingRespDTO>> queryList(@RequestBody TrackingReqDTO dto);


    @Component
    static class HystrixClientFallback implements TrackingFeignClient {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);


        @SuppressWarnings("rawtypes")
        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
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
        public JSONResult<Boolean> deleteTracking(IdListLongReq idListReq) {
            return fallBackError("资源跟进记录-删除");
        }

        @Override
        public JSONResult<List<TrackingRespDTO>> queryList(TrackingReqDTO dto) {
            return fallBackError("资源跟进记录-查询（不分页）");
        }
    }

}
