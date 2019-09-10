package com.kuaidao.manageweb.feign.merchant.publiccustomer;

import com.kuaidao.aggregation.dto.pubcusres.ClueQueryParamDTO;
import com.kuaidao.aggregation.dto.pubcusres.PublicCustomerResourcesReqDTO;
import com.kuaidao.aggregation.dto.pubcusres.PublicCustomerResourcesRespDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.publiccustomer.PublicCustomerFeignClient.HystrixClientFallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 *
 * 功能描述: 
 *      公共客户资源-商家版
 * @auther  yangbiao
 * @date: 2019/1/8 17:35
 */
@FeignClient(name = "merchant-service",path="/merchant/pubcustomer",fallback = HystrixClientFallback.class)
public interface PublicCustomerFeignClient {


    @PostMapping("/queryPage")
    public JSONResult<PageBean<PublicCustomerResourcesRespDTO>> queryListPage(
        @RequestBody ClueQueryParamDTO dto);


    @Component
    static class HystrixClientFallback implements
        PublicCustomerFeignClient {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        @SuppressWarnings("rawtypes")
        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }



        @Override
        public JSONResult<PageBean<PublicCustomerResourcesRespDTO>> queryListPage(ClueQueryParamDTO dto) {
            return fallBackError("公共客户资源分页查询");
        }


    }

}
