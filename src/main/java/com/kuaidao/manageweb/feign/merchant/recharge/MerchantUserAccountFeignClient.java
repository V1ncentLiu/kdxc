package com.kuaidao.manageweb.feign.merchant.recharge;

import com.kuaidao.account.dto.recharge.MerchantUserAccountDTO;
import com.kuaidao.account.dto.recharge.MerchantUserAccountQueryDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
* @Description 商家账户余额
* @Author xuyunfeng
* @Date 2019/9/25 17:37
**/
@FeignClient(name = "account-service", path = "/account/merchantUserAccount",
        fallback = MerchantUserAccountFeignClient.HystrixClientFallback.class)
public interface MerchantUserAccountFeignClient {

    /**
     * 根据商家账号Id获取账号余额信息
     * 
     * @param dto
     * @return
     */
    @PostMapping("/getMerchantUserAccountInfo")
    public JSONResult<MerchantUserAccountDTO> getMerchantUserAccountInfo(
        @RequestBody MerchantUserAccountQueryDTO dto);


    @Component
    static class HystrixClientFallback implements
        MerchantUserAccountFeignClient {

        private static Logger logger = LoggerFactory.getLogger(
            MerchantUserAccountFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult<MerchantUserAccountDTO> getMerchantUserAccountInfo(@RequestBody MerchantUserAccountQueryDTO dto) {
            return fallBackError("查询规则报表集合");
        }



    }


}
