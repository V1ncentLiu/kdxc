package com.kuaidao.manageweb.feign.merchant.consumerecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.account.dto.balancewarn.BalanceWarnSettingDTO;
import com.kuaidao.account.dto.balancewarn.BalanceWarnSettingReq;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import feign.hystrix.FallbackFactory;

/**
 * 消费记录
 * 
 * @author: zxy
 * @date: 2019年3月14日
 * @version V1.0
 */
@FeignClient(name = "account-service", path = "/account/balanceWarnSetting",
        fallbackFactory = BalanceWarnSettingFeignClient.HystrixClientFallback.class)
public interface BalanceWarnSettingFeignClient {
    /**
     * 余额不足提醒设置
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/setting")
    public JSONResult<Long> setting(@RequestBody BalanceWarnSettingReq balanceWarnSettingReq);



    /**
     * 根据id查询消费记录信息
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/getBalanceWarnSetting")
    public JSONResult<BalanceWarnSettingDTO> getBalanceWarnSetting();



    @Component
    static class HystrixClientFallback implements FallbackFactory<BalanceWarnSettingFeignClient> {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        @Override
        public BalanceWarnSettingFeignClient create(Throwable cause) {
            return new BalanceWarnSettingFeignClient() {
                @SuppressWarnings("rawtypes")
                private JSONResult fallBackError(String name) {
                    logger.error("接口调用失败");
                    logger.error("接口名{}", name);
                    logger.error("失败原因{}", cause);
                    return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                            SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
                }


                @Override
                public JSONResult<Long> setting(BalanceWarnSettingReq balanceWarnSettingReq) {
                    return fallBackError("设置余额不足提醒");
                }

                @Override
                public JSONResult<BalanceWarnSettingDTO> getBalanceWarnSetting() {
                    return fallBackError("查询余额不足提醒设置");
                }

            };
        }


    }

}
