package com.kuaidao.manageweb.feign.clue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.kuaidao.aggregation.dto.clue.CustomerManagerDTO;
import com.kuaidao.aggregation.dto.clue.CustomerManagerQueryDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.assignrule.InfoAssignFeignClient;

@FeignClient(name = "aggregation-service", path = "/aggregation/clue/customerManager",
        fallback = CustomerManagerFeignClient.HystrixClientFallback.class)
public interface CustomerManagerFeignClient {

    /**
     * 客户管理分页查询
     * 
     * @param queryDTO
     * @return
     */

    @RequestMapping(method = RequestMethod.POST, value = "/findcustomerPage")
    JSONResult<PageBean<CustomerManagerDTO>> findcustomerPage(CustomerManagerQueryDTO queryDTO);



    @Component
    static class HystrixClientFallback implements CustomerManagerFeignClient {

        private static Logger logger = LoggerFactory.getLogger(InfoAssignFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<PageBean<CustomerManagerDTO>> findcustomerPage(
                CustomerManagerQueryDTO queryDTO) {
            // TODO Auto-generated method stub
            return fallBackError("查询客户管理分页数据");
        }


    }

}
