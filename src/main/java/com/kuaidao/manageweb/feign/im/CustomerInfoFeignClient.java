package com.kuaidao.manageweb.feign.im;

import com.kuaidao.aggregation.dto.financing.*;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.*;
import com.kuaidao.im.dto.custservice.CustomerInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 *  退返款 
 * @author  Chen
 * @date 2019年4月10日 下午7:35:14   
 * @version V1.0
 */
@FeignClient(name = "cust-service-service", path = "/custservice/customerInfo", fallback = CustomerInfoFeignClient.HystrixClientFallback.class)
public interface CustomerInfoFeignClient {

    @PostMapping(value = "/brandAndIssubmit")
    JSONResult<List<CustomerInfoDTO>> brandAndIssubmit(@RequestBody IdListReq ids);

    @Component
    static class HystrixClientFallback implements CustomerInfoFeignClient {

        private static Logger logger = LoggerFactory.getLogger(CustomerInfoFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult<List<CustomerInfoDTO>> brandAndIssubmit(IdListReq ids) {
            return fallBackError("品牌信息以及是否提交接口");
        }
    }


}
