package com.kuaidao.manageweb.feign.merchant.bussinesscall;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 资源资费
 *
 * @version V1.0
 * @author: zxy
 * @date: 2019年1月4日
 */
@FeignClient(name = "account-service", path = "/account/call/package/job",
        fallbackFactory = CallPackageJobFeignClient.HystrixClientFallback.class)
public interface CallPackageJobFeignClient {

    /**
     * 每5分钟执行 （排除晚上10-早9点）范围
     * 1.定时发短信
     *
     * @return
     */
    @PostMapping("/schedule/send/msg")
    JSONResult<String> scheduleSendMsg() ;


    /**
     * 每月1日1点开始执行
     * 2.定时更新用户套餐 done
     * 3.定时扣话费      done
     *
     * @return
     */
    @PostMapping("/schedule/update/package")
     JSONResult<String> scheduleUpdatePackage() ;


    /**
     * 4.定时扣话费
     */
    @PostMapping("/schedule/deduct/call")
     JSONResult<String> scheduleDeductCall() ;

    /**
     * 5.定时扣费，解封账户
     *
     * @return
     */
    @PostMapping("/schedule/deduct/arrears")
    JSONResult<String> scheduleDeductArrears() ;
    /**
     * 6.失败重试记录表
     *
     * @return
     */
    @PostMapping("/schedule/fail/retry")
    JSONResult<String> scheduleFailReTry() ;


    /**
     * 7.定时拉取通话记录
     */
    @PostMapping("/schedule/pull/callRecord")
    JSONResult<String> schedulePullCallRecord() ;

    @PostMapping("/schedule/deduct/callCost")
    JSONResult<String> scheduleDeductCallCost(@RequestParam("day") String day);


    @PostMapping("/deduct/package")
    JSONResult<String> deductPackage(@RequestParam("endTime") String endTime);


    @Component
    class HystrixClientFallback implements FallbackFactory<CallPackageJobFeignClient> {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        @Override
        public CallPackageJobFeignClient create(Throwable cause) {
            return new CallPackageJobFeignClient() {


                @Override
                public JSONResult<String> scheduleSendMsg() {
                    return fallBackError("定时发送余额不足提醒失败");
                }

                @Override
                public JSONResult<String> scheduleUpdatePackage() {
                    return fallBackError("每月更新套餐失败");
                }

                @Override
                public JSONResult<String> scheduleDeductCall() {
                    return fallBackError("每天扣除前天通话费用失败");
                }

                @Override
                public JSONResult<String> scheduleDeductArrears() {
                    return fallBackError("定时处理欠费套餐失败");
                }

                @Override
                public JSONResult<String> scheduleFailReTry() {
                    return fallBackError("失败重试");
                }

                @Override
                public JSONResult<String> schedulePullCallRecord() {
                    return fallBackError("定期来去通话记录");
                }

                @Override
                public JSONResult<String> scheduleDeductCallCost(String day) {
                     return fallBackError("扣除指定时间费用话费");
                }

                @Override
                public JSONResult<String> deductPackage(String endTime) {
                    return fallBackError("扣除套餐费用");
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
