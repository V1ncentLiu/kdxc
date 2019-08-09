package com.kuaidao.manageweb.feign.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.kuaidao.callcenter.dto.HeLiClientOutboundReqDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;


/**
 * 合力 坐席接口
 * @author  Devin.Chen
 * @date 2019-08-08 10:39:01
 * @version V1.0
 */
@FeignClient(name = "callcenter-service-chen", path = "/callcenter/heliClient/", fallback = HeliClientFeignClient.HystrixClientFallback.class)
public interface HeliClientFeignClient {
    
    
    /**
     * 坐席登录 
    * @param heLiClientOutboundReqDTO
    * @return
     */
    @SuppressWarnings("rawtypes")
    @PostMapping("/login")
    public JSONResult login (@RequestBody HeLiClientOutboundReqDTO  heLiClientOutboundReqDTO);
    
    
    /**
     * 坐席退出 
    * @param heLiClientOutboundReqDTO
    * @return
     */
    @SuppressWarnings("rawtypes")
    @PostMapping("/logout")
    public JSONResult logout (@RequestBody HeLiClientOutboundReqDTO  heLiClientOutboundReqDTO);
    
    /**
     * 外呼
    * @param heLiClientOutboundReqDTO
    * @return
     */
    @PostMapping("/outbound")
    public JSONResult outbound(@RequestBody HeLiClientOutboundReqDTO heLiClientOutboundReqDTO);

    
    @Component
    static class HystrixClientFallback implements HeliClientFeignClient {

        private static Logger logger = LoggerFactory.getLogger(ClientFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult login(HeLiClientOutboundReqDTO heLiClientOutboundReqDTO) {
            return fallBackError("合力坐席登录");
        }

        @Override
        public JSONResult logout(HeLiClientOutboundReqDTO heLiClientOutboundReqDTO) {
            return fallBackError("合力坐席退出");
        }

        @Override
        public JSONResult outbound(HeLiClientOutboundReqDTO heLiClientOutboundReqDTO) {
            return fallBackError("合力坐席外呼");
        }
        
    }


}
