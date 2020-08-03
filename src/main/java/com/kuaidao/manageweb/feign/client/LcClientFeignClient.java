package com.kuaidao.manageweb.feign.client;

import com.kuaidao.callcenter.dto.LcClient.AddOrUpdateLcClientDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(name = "callcenter-service", path = "/callcenter/lcClient", fallback = LcClientFeignClient.HystrixClientFallback.class)
public interface LcClientFeignClient {


    @PostMapping("/saveLcClient")
    public JSONResult<Boolean> saveLcClient(@RequestBody AddOrUpdateLcClientDTO reqDTO);

    @PostMapping("/updateLcClient")
    public JSONResult<Boolean> updateLcClient(@RequestBody AddOrUpdateLcClientDTO reqDTO);

    @Component
    static class HystrixClientFallback implements LcClientFeignClient {
        private static Logger logger = LoggerFactory.getLogger(LcClientFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Boolean> saveLcClient(@Valid AddOrUpdateLcClientDTO reqDTO) {
            return fallBackError("保存乐创坐席");
        }

        @Override
        public JSONResult<Boolean> updateLcClient(@Valid AddOrUpdateLcClientDTO reqDTO) {
            return fallBackError("保存修改坐席");
        }
    }


}
