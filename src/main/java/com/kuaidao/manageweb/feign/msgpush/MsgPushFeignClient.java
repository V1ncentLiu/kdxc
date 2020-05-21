package com.kuaidao.manageweb.feign.msgpush;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.msgpush.dto.SmsCodeAndMobileValidReq;
import com.kuaidao.msgpush.dto.SmsCodeSendReq;
import com.kuaidao.msgpush.dto.SmsTemplateCodeReq;
import com.kuaidao.msgpush.dto.SmsVoiceCodeReq;

/**
 * 消息推送
 * 
 * @author: zxy
 * @date: 2019年1月4日
 * @version V1.0
 */
@FeignClient(name = "msg-push-service-1", path = "/msgpush/v1.0/sms",
        fallback = MsgPushFeignClient.HystrixClientFallback.class)
public interface MsgPushFeignClient {


    /**
     * 校验验证码
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/validCodeAndMobile")
    public JSONResult validCodeAndMobile(
            @RequestBody SmsCodeAndMobileValidReq smsCodeAndMobileValidReq);


    /**
     * 发送短信验证
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/sendCode")
    public JSONResult<String> sendCode(@RequestBody SmsCodeSendReq smsCodeSendReq);

    /**
     * 发送语音验证
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/sendVoiceCode")
    public JSONResult<String> sendVoiceCode(@RequestBody SmsVoiceCodeReq voiceCodeReq);


    @PostMapping("/sendTempSms")
    public JSONResult<String> sendTempSms(@RequestBody SmsTemplateCodeReq smsTemplateCodeReq);

    @Component
    static class HystrixClientFallback implements MsgPushFeignClient {

        private static Logger logger = LoggerFactory.getLogger(MsgPushFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }



        @Override
        public JSONResult validCodeAndMobile(
                @RequestBody SmsCodeAndMobileValidReq smsCodeAndMobileValidReq) {
            return fallBackError("校验验证码");
        }


        @Override
        public JSONResult<String> sendCode(@RequestBody SmsCodeSendReq smsCodeSendReq) {
            return fallBackError("发送短信验证码");
        }

        @Override
        public JSONResult<String> sendVoiceCode(@RequestBody SmsVoiceCodeReq voiceCodeReq) {
            return fallBackError("发送语音验证码");
        }

        @Override
        public JSONResult<String> sendTempSms(SmsTemplateCodeReq smsTemplateCodeReq) {
            return fallBackError("发送模板短信");
        }


    }

}
