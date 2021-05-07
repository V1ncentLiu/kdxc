package com.kuaidao.manageweb.feign.clue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.kuaidao.aggregation.dto.clue.ClueRelateReq;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;

/**
 * 线索-所属组织
 * 
 * @author: fanjd
 * @date: 2019年6月17日
 * @version V1.0
 */
@FeignClient(name = "aggregation-service", path = "/aggregation/clue/clueRelate", fallback = ClueRelateFeignClient.HystrixClientFallback.class)
public interface ClueRelateFeignClient {

    /**
     * 根据创业顾问id更新所在电销组组织信息
     * 
     * @author: Fanjd
     * @param clueRelateReq
     * @return:
     * @Date: 2021/05/07 16:12
     * @since: 1.0.0
     **/
    @PostMapping("/updateClueRelateByAgentSaleId")

    JSONResult<String> updateClueRelateByAgentSaleId(@RequestBody ClueRelateReq clueRelateReq);

    /**
     * 根据创业顾问id更新所在电销组组织信息
     *
     * @author: Fanjd
     * @param clueRelateReq
     * @return:
     * @Date: 2019/6/17 16:12
     * @since: 1.0.0
     **/
    @PostMapping("/updateClueRelateByTeleSaleId")

    JSONResult<String> updateClueRelateByTeleSaleId(@RequestBody ClueRelateReq clueRelateReq);

    @Component
    class HystrixClientFallback implements ClueRelateFeignClient {

        private static Logger logger = LoggerFactory.getLogger(ClueRelateFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(), SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult<String> updateClueRelateByAgentSaleId(ClueRelateReq clueRelateReq) {
            return fallBackError("根据加盟经纪id更新所在电销组组织信息失败");
        }

        @Override
        public JSONResult<String> updateClueRelateByTeleSaleId(@RequestBody ClueRelateReq clueRelateReq) {
            return fallBackError("根据创业顾问id更新所在电销组组织信息失败");
        }



    }



}
