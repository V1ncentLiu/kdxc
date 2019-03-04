package com.kuaidao.manageweb.feign.sign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.aggregation.dto.busmycustomer.RejectSignOrderReqDTO;
import com.kuaidao.aggregation.dto.busmycustomer.SignRecordReqDTO;
import com.kuaidao.aggregation.dto.busmycustomer.SignRecordRespDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.client.ClientFeignClient;


/**
 * 签约记录
 * @author  Chen
 * @date 2019年3月1日 下午6:36:23   
 * @version V1.0
 */
@FeignClient(name = "aggregation-service-chen", path = "/aggregation/sign/signRecord", fallback = SignRecordFeignClient.HystrixClientFallback.class)
public interface SignRecordFeignClient {
    
    /**
     * 查询 签约记录
     * @param reqDTO
     * @return
     */
    @PostMapping("/listSignRecord")
    JSONResult<PageBean<SignRecordRespDTO>> listSignRecord(@RequestBody SignRecordReqDTO reqDTO);

    /**
     * 签约记录驳回
     * @param reqDTO
     * @return
     */
    @PostMapping("/rejectSignOrder")
    JSONResult<Boolean> rejectSignOrder(@RequestBody RejectSignOrderReqDTO reqDTO);
    
    @Component
    static class HystrixClientFallback implements SignRecordFeignClient {

        private static Logger logger = LoggerFactory.getLogger(ClientFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<PageBean<SignRecordRespDTO>> listSignRecord(SignRecordReqDTO reqDTO) {
            return fallBackError("查询签约记录");
        }

        @Override
        public JSONResult<Boolean> rejectSignOrder(RejectSignOrderReqDTO reqDTO) {
            return fallBackError("签约记录驳回");
        }
    }

}

