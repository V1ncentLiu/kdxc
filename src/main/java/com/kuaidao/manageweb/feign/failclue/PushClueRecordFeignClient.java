package com.kuaidao.manageweb.feign.failclue;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.publish.dto.clue.PushClueRecord;

/**
 * 资源分配规则
 * 
 * @author: zxy
 * @date: 2019年3月14日
 * @version V1.0
 */
@FeignClient(name = "aggregation-service", path = "/aggregation/pushClueRecord",
        fallback = PushClueRecordFeignClient.HystrixClientFallback.class)
public interface PushClueRecordFeignClient {

    /**
     * 失败资源记录 ，参数模糊匹配
     *
     * @return
     */
    @PostMapping("/list")
    public JSONResult<List<PushClueRecord>> list(@RequestBody PushClueRecord pushClueRecord);



    @Component
    static class HystrixClientFallback implements PushClueRecordFeignClient {

        private static Logger logger = LoggerFactory.getLogger(PushClueRecordFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult<List<PushClueRecord>> list(PushClueRecord pushClueRecord) {
            return fallBackError("失败资源记录集合");
        }

    }


}
