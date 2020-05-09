package com.kuaidao.manageweb.feign.merchant.consumerecord;


import com.kuaidao.account.dto.consume.MonthConsumeStatisticsDTO;
import com.kuaidao.account.dto.consume.MonthConsumeStatisticsReq;
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

@FeignClient(name = "account-service", path = "/account/monthConsumeStatistics",
        fallbackFactory = MonthConsumeStatisticsFeignClient.HystrixClientFallback.class)
public interface MonthConsumeStatisticsFeignClient {


    @PostMapping("/list")
    public JSONResult<PageBean<MonthConsumeStatisticsDTO>> list(@RequestBody MonthConsumeStatisticsReq pageParam);

    @Component
    static class HystrixClientFallback
            implements FallbackFactory<MonthConsumeStatisticsFeignClient> {

        private static Logger logger = LoggerFactory.getLogger(MonthConsumeStatisticsFeignClient.HystrixClientFallback.class);

        @Override
        public MonthConsumeStatisticsFeignClient create(Throwable cause) {
            return new MonthConsumeStatisticsFeignClient() {
                @Override
                public JSONResult<PageBean<MonthConsumeStatisticsDTO>> list(MonthConsumeStatisticsReq pageParam) {
                    return fallBackError("查询商家月消费记录统计集合");
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
