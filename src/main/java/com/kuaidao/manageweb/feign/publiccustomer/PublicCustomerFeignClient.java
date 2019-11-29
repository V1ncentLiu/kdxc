package com.kuaidao.manageweb.feign.publiccustomer;

import com.kuaidao.aggregation.dto.pubcusres.ClueQueryParamDTO;
import com.kuaidao.aggregation.dto.pubcusres.PublicCustomerResourcesReqDTO;
import com.kuaidao.aggregation.dto.pubcusres.PublicCustomerResourcesRespDTO;
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
 *      公共客户资源
 * @auther  yangbiao
 * @date: 2019/1/8 17:35
 */
@FeignClient(name = "aggregation-service",path="/aggregation/publicCustomerResources",fallback = PublicCustomerFeignClient.HystrixClientFallback.class)
public interface PublicCustomerFeignClient {

    @PostMapping("/allocationResource")
    public JSONResult<Boolean>   allocationResource(@RequestBody PublicCustomerResourcesReqDTO dto);
    @PostMapping("/releaseRecord")
    public JSONResult<PageBean> releaseRecord(@RequestBody PublicCustomerResourcesReqDTO dto);
    @PostMapping("/transferOfResource")
    public JSONResult<Boolean>   transferOfResource(@RequestBody PublicCustomerResourcesReqDTO dto);
    @PostMapping("/resourceReduction")
    public JSONResult<Boolean> resourceReduction(@RequestBody PublicCustomerResourcesReqDTO dto);
    @PostMapping("/queryPage")
    public JSONResult<PageBean<PublicCustomerResourcesRespDTO>> queryListPage(@RequestBody ClueQueryParamDTO dto);
    @PostMapping("/repeatPhones")
    public JSONResult<PageBean> repeatPhones(@RequestBody PublicCustomerResourcesReqDTO dto);
    @PostMapping("/followUpRecord")
    public JSONResult<PageBean> followUpRecord(@RequestBody PublicCustomerResourcesReqDTO dto);

    @Component
    static class HystrixClientFallback implements PublicCustomerFeignClient {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        @SuppressWarnings("rawtypes")
        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Boolean> allocationResource(PublicCustomerResourcesReqDTO dto) {
            return fallBackError("公共资源-资源分配");
        }

        @Override
        public JSONResult<PageBean> releaseRecord(PublicCustomerResourcesReqDTO dto) {
            return fallBackError("公共资源-释放记录");
        }

        @Override
        public JSONResult<Boolean> transferOfResource(PublicCustomerResourcesReqDTO dto) {
            return fallBackError("公共资源-资源转移");
        }

        @Override
        public JSONResult<Boolean> resourceReduction(PublicCustomerResourcesReqDTO dto) {
            return fallBackError("公共资源-资源还原");
        }

        @Override
        public JSONResult<PageBean<PublicCustomerResourcesRespDTO>> queryListPage(ClueQueryParamDTO dto) {
            return fallBackError("公共客户资源分页查询");
        }

        @Override
        public JSONResult<PageBean> repeatPhones(PublicCustomerResourcesReqDTO dto) {
            return fallBackError("重复手机号");
        }

        @Override
        public JSONResult<PageBean> followUpRecord(PublicCustomerResourcesReqDTO dto) {
            return fallBackError("跟进记录");
        }
    }

}
