package com.kuaidao.manageweb.feign.InvalidCustomer;

import com.kuaidao.aggregation.dto.invalidcustomer.ClueQueryParamDTO;
import com.kuaidao.aggregation.dto.invalidcustomer.InvalidCustomerResourcesReqDTO;
import com.kuaidao.aggregation.dto.invalidcustomer.InvalidCustomerResourcesRespDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 *
 * 功能描述: 
 *      无效客户资源
 * @auther  yangbiao
 * @date: 2019/1/8 17:35
 */
@FeignClient(name = "aggregation-service",path="/aggregation/invalidCustomerResources",fallback = InvalidCustomerFeignClient.HystrixClientFallback.class)
public interface InvalidCustomerFeignClient {

    @PostMapping("/releaseRecord")
    public JSONResult<PageBean> releaseRecord(@RequestBody InvalidCustomerResourcesReqDTO dto);
    @PostMapping("/resourceReduction")
    public JSONResult<Boolean> resourceReduction(@RequestBody InvalidCustomerResourcesReqDTO dto);
    @PostMapping("/queryPage")
    public JSONResult<PageBean<InvalidCustomerResourcesRespDTO>> queryListPage(@RequestBody ClueQueryParamDTO dto);

    @Component
    static class HystrixClientFallback implements InvalidCustomerFeignClient {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        @SuppressWarnings("rawtypes")
        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<PageBean> releaseRecord(InvalidCustomerResourcesReqDTO dto) {
            return fallBackError("无效客户资源-释放记录");
        }

        @Override
        public JSONResult<Boolean> resourceReduction(InvalidCustomerResourcesReqDTO dto) {
            return fallBackError("无效客户资源-资源还原");
        }

        @Override
        public JSONResult<PageBean<InvalidCustomerResourcesRespDTO>> queryListPage(ClueQueryParamDTO dto) {
            return fallBackError("无效客户资源分页查询");
        }
    }
}
