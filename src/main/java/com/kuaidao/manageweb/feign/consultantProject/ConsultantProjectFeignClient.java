package com.kuaidao.manageweb.feign.consultantProject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;

/**
 * 项目设置
 */
@FeignClient(name = "agent-service", path = "/agentservice/agentConsultantProject",
        fallback = ConsultantProjectFeignClient.HystrixClientFallback.class)
public interface ConsultantProjectFeignClient {

    @RequestMapping("/deleteByConsultantId")
    JSONResult<Boolean> deleteByConsultantId(@RequestBody IdEntityLong idEntityLong);

    @Component
    static class HystrixClientFallback implements ConsultantProjectFeignClient {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        @SuppressWarnings("rawtypes")
        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(), SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Boolean> deleteByConsultantId(IdEntityLong idEntityLong) {
            return fallBackError("根据客户删除项目设置");
        }

    }

}
