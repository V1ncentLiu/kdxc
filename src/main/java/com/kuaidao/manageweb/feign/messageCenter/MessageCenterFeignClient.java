package com.kuaidao.manageweb.feign.messageCenter;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * 这里
 * 功能描述:
 *      消息中心-业务中心
 * @auther: yangbiao
 * @date: 2019/1/8 17:35
 */
@FeignClient(name = "sys-service",path="/sys/mcenter",fallback = MessageCenterFeignClient.HystrixClientFallback.class)
public interface MessageCenterFeignClient {


    @PostMapping("/unreadCount")
    public JSONResult unreadCount(@RequestBody Map map);

    @Component
    static class HystrixClientFallback implements MessageCenterFeignClient {


        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);


        @SuppressWarnings("rawtypes")
        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult unreadCount(Map map) {
            return fallBackError("消息中心：未读消息数据获取失败");
        }
    }
}
