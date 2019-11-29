package com.kuaidao.manageweb.feign.clue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;

/**
 * 商务定时任务
 * 
 * @author: zhangxingyu
 * @date: 2019年1月4日
 * @version V1.0
 */
@FeignClient(name = "aggregation-service", path = "/aggregation/business/schedule",
        fallback = BusScheduleFeignClient.HystrixClientFallback.class)
public interface BusScheduleFeignClient {


    /**
     * 到访记录提醒
     *
     * @param commentInsertReq
     * @return
     */
    @PostMapping("/visitRecordReminder")
    public JSONResult visitRecordReminder();

    /**
     * 签约记录提醒
     *
     * @param commentInsertReq
     * @return
     */
    @PostMapping("/signRecordReminder")
    public JSONResult signRecordReminder();



    /**
     * 到访记录提醒(商务经理) 定时任务
     */
    @PostMapping("/visitRecordToBusSaleReminder")
    public JSONResult notVisitRecordReminder();
    /**
     * 签约记录提醒(商务经理) 定时任务
     * @return
     */
    @PostMapping("/signRecordToBusSaleIdReminder")
    public JSONResult signRecordToSaleIdReminder();



    @Component
    static class HystrixClientFallback implements BusScheduleFeignClient {

        private static Logger logger = LoggerFactory.getLogger(BusScheduleFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult visitRecordReminder() {
            return fallBackError("到访记录提醒");
        }

        @Override
        public JSONResult signRecordReminder() {
            return fallBackError("签约记录提醒");
        }

        @Override
        public JSONResult notVisitRecordReminder() {
            return fallBackError("到访记录(商务经理)提醒");
        }

        @Override
        public JSONResult signRecordToSaleIdReminder() {
            return fallBackError("签约记录(商务经理)提醒");
        }

    }


}
