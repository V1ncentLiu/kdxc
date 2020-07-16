package com.kuaidao.manageweb.feign.deduplicationDetail;


import com.kuaidao.aggregation.dto.clue.PushClueReq;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.feign.dashboard.DashboardTeleGroupFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "aggregation-service", path = "/aggregation/deduplicationDetail", fallback = DeduplicationDetailFeignClient.HystrixClientFallback.class)
public interface DeduplicationDetailFeignClient {

    /**
     * 根据手机号、微信号、天数、业务线是否存在重复数据
     * true 存在重复
     * false 不存在
     */
    @PostMapping("/getClueByParam")
    public JSONResult<Boolean> getClueByParam(@RequestBody PushClueReq pushClueReq);

    @Component
    class HystrixClientFallback implements DeduplicationDetailFeignClient {

        private static Logger logger = LoggerFactory.getLogger(DashboardTeleGroupFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(), SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Boolean> getClueByParam(PushClueReq pushClueReq) {
            return fallBackError("根据手机号、微信号、天数、业务线是否存在重复数据");
        }
    }


}
