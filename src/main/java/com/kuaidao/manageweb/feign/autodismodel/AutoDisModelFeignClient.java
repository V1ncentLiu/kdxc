package com.kuaidao.manageweb.feign.autodismodel;


import com.kuaidao.businessconfig.dto.automodel.AutoDisModelDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 自动分配模型Feign类
 *
 * @author fengyixuan
 */
@FeignClient(name = "business-config-service", path = "/businessConfig/autodisModel", fallback = AutoDisModelFeignClient.HystrixClientFallback.class)
public interface AutoDisModelFeignClient {

    @RequestMapping(method = RequestMethod.POST, value = "/update")
    JSONResult<Boolean> update(@RequestBody AutoDisModelDTO dto);

    @RequestMapping(method = RequestMethod.POST, value = "/insert")
    JSONResult<Boolean> insert(@RequestBody AutoDisModelDTO dto);

    @RequestMapping(method = RequestMethod.POST, value = "/queryByParams")
    JSONResult<AutoDisModelDTO> queryByParams(@RequestBody AutoDisModelDTO dto);

    @Component
    static class HystrixClientFallback implements AutoDisModelFeignClient {

        private static final Logger logger = LoggerFactory.getLogger(AutoDisModelFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult<Boolean> update(AutoDisModelDTO dto) {
            return fallBackError("自动分配模型-更新");
        }

        @Override
        public JSONResult<Boolean> insert(AutoDisModelDTO dto) {
            return fallBackError("自动分配模型-插入");
        }

        @Override
        public JSONResult<AutoDisModelDTO> queryByParams(AutoDisModelDTO dto) {
            return fallBackError("自动分配模型-查询");
        }
    }

}
