package com.kuaidao.manageweb.feign.failclue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdListReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.publish.dto.clue.FailCluePageParam;
import com.kuaidao.publish.dto.clue.FailPushClue;

/**
 * 资源分配规则
 * 
 * @author: zxy
 * @date: 2019年3月14日
 * @version V1.0
 */
@FeignClient(name = "aggregation-service", path = "/aggregation/failClue",
        fallback = FailClueFeignClient.HystrixClientFallback.class)
public interface FailClueFeignClient {

    /**
     * 失败资源记录 ，参数模糊匹配
     *
     * @return
     */
    @PostMapping("/failClueRecord")
    public JSONResult<PageBean<FailPushClue>> failClueRecord(
            @RequestBody FailCluePageParam failCluePageParam);

    /**
     * 手动重新推送资源
     *
     * @return
     */
    @PostMapping("/pushFailClue")
    public JSONResult<String> pushFailClue(@RequestBody IdListReq idListReq);



    @Component
    static class HystrixClientFallback implements FailClueFeignClient {

        private static Logger logger = LoggerFactory.getLogger(FailClueFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult<PageBean<FailPushClue>> failClueRecord(
                @RequestBody FailCluePageParam failCluePageParam) {
            return fallBackError("失败资源记录集合");
        }

        @Override
        public JSONResult<String> pushFailClue(@RequestBody IdListReq idListReq) {
            return fallBackError("重新推送资源");
        }



    }


}
