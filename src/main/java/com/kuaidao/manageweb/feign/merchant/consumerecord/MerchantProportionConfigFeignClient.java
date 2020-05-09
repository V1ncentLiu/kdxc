package com.kuaidao.manageweb.feign.merchant.consumerecord;


import com.kuaidao.account.dto.config.MerchantProportionConfigDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name = "account-service", path = "/account/merchantProportionConfig",
        fallbackFactory = MerchantProportionConfigFeignClient.HystrixClientFallback.class)
public interface MerchantProportionConfigFeignClient {


    @PostMapping("/findListAll")
    public JSONResult<List<MerchantProportionConfigDTO>> list();

    @RequestMapping("/saveOrUpdate")
    public JSONResult saveOrUpdate(@RequestBody MerchantProportionConfigDTO merchantProportionConfigDTO);

    @Component
    static class HystrixClientFallback
            implements FallbackFactory<MerchantProportionConfigFeignClient> {

        private static Logger logger = LoggerFactory.getLogger(MerchantProportionConfigFeignClient.HystrixClientFallback.class);

        @Override
        public MerchantProportionConfigFeignClient create(Throwable cause) {
            return new MerchantProportionConfigFeignClient() {

                @Override
                public JSONResult<List<MerchantProportionConfigDTO>> list() {
                    return fallBackError("分公司消费占比配置");
                }

                @Override
                public JSONResult saveOrUpdate(MerchantProportionConfigDTO merchantProportionConfigDTO) {
                    return fallBackError("分公司消费占比配置保存");
                }

                @SuppressWarnings("rawtypes")
                private JSONResult fallBackError(String name) {
                    logger.error("接口调用失败");
                    logger.error("接口名{}", name);
                    logger.error("失败原因{}", cause);
                    return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                            SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
                }

            };
        }
    }

}
