package com.kuaidao.manageweb.feign.clue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.kuaidao.aggregation.dto.clue.TelCreatePhoneLogDto;
import com.kuaidao.aggregation.dto.clue.TelCreatePhoneLogReqDto;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;

/**
 * 新建手机号查询
 * @author: fanjd
 * @date: 2020年6月18日
 * @version V1.0
 */
@FeignClient(name = "aggregation-service-1", path = "/aggregation/telCreatePhoneLog",
        fallback = TelCreatePhoneLogFeignClient.HystrixClientFallback.class)
public interface TelCreatePhoneLogFeignClient {

    /**
     * 新建手机号查询列表
     * @param
     * @return
     */
    @PostMapping("/queryPageList")
    JSONResult<PageBean<TelCreatePhoneLogDto>> queryPageList(@RequestBody TelCreatePhoneLogReqDto reqDTO);

    @Component
    static class HystrixClientFallback implements TelCreatePhoneLogFeignClient {

        private static Logger logger = LoggerFactory.getLogger(TelCreatePhoneLogFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(), SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<PageBean<TelCreatePhoneLogDto>> queryPageList(TelCreatePhoneLogReqDto reqDTO) {
            return fallBackError("新建手机号查询列表");
        }
    }

}
