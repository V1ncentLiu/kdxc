package com.kuaidao.manageweb.feign.preference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kuaidao.businessconfig.dto.telepreference.TelePreferenceSetDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;

/**
 * 自动分配模型Feign类
 * @author fengyixuan
 */
@FeignClient(name = "business-config-service", path = "/businessConfig/telepreference", fallback = PreferenceFeignClient.HystrixClientFallback.class)
public interface PreferenceFeignClient {

    @RequestMapping(method = RequestMethod.POST, value = "/update")
    JSONResult<Boolean> update(@RequestBody TelePreferenceSetDTO dto);

    @RequestMapping(method = RequestMethod.POST, value = "/insert")
    JSONResult<Boolean> insert(@RequestBody TelePreferenceSetDTO dto);

    @RequestMapping(method = RequestMethod.POST, value = "/queryByParams")
    JSONResult<PageBean<TelePreferenceSetDTO>> queryByParams(@RequestBody TelePreferenceSetDTO dto);

    @RequestMapping(method = RequestMethod.POST, value = "/updateBusyStatus")
    JSONResult<Boolean> updateBusyStatus(@RequestBody TelePreferenceSetDTO dto);

    @RequestMapping(method = RequestMethod.POST, value = "/queryBusyStatus")
    public JSONResult<TelePreferenceSetDTO> queryBusyStatus(@RequestBody IdEntityLong id);

    @Component
    static class HystrixClientFallback implements PreferenceFeignClient {

        private static final Logger logger = LoggerFactory.getLogger(PreferenceFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(), SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Boolean> update(TelePreferenceSetDTO dto) {
            return fallBackError("偏好设置-更新接口");
        }

        @Override
        public JSONResult<Boolean> insert(TelePreferenceSetDTO dto) {
            return fallBackError("偏好设置-新增接口");
        }

        @Override
        public JSONResult<PageBean<TelePreferenceSetDTO>> queryByParams(TelePreferenceSetDTO dto) {
            return fallBackError("偏好设置列表查询接口");
        }

        @Override
        public JSONResult<Boolean> updateBusyStatus(TelePreferenceSetDTO dto) {
            return fallBackError("忙碌状态设置接口");
        }

        @Override
        public JSONResult<TelePreferenceSetDTO> queryBusyStatus(IdEntityLong id) {
            return fallBackError("忙碌状态查询接口");
        }
    }

}
