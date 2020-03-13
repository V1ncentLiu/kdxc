package com.kuaidao.manageweb.feign.financing;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.kuaidao.aggregation.dto.financing.ApplyRefundRebateReq;
import com.kuaidao.aggregation.dto.financing.RefundRebateListDTO;
import com.kuaidao.aggregation.dto.financing.RefundRebatePageParam;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;

/**
 * 退返款申请
 * 
 * @author: zxy
 * @date: 2019年1月4日
 * @version V1.0
 */
@FeignClient(name = "aggregation-service", path = "/aggregation/refundRebate",
        fallback = OverCostFeignClient.HystrixClientFallback.class)
public interface OverCostFeignClient {

    /**
     * 退返款申请列表
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/list")
    public JSONResult<PageBean<RefundRebateListDTO>> list(@RequestBody RefundRebatePageParam param);

    /**
     * 关联签约单列表
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/rebatesignList")
    public JSONResult<List<RefundRebateListDTO>> rebatesignList(
            @RequestBody RefundRebatePageParam param);


    /**
     * 餐饮公司申请退返款
     * 
     * @param idEntity
     * @return
     */
    @PostMapping("/applyRefundRebate")
    public JSONResult<Long> applyRefundRebate(@RequestBody ApplyRefundRebateReq req);



    @Component
    static class HystrixClientFallback implements OverCostFeignClient {

        private static Logger logger = LoggerFactory.getLogger(OverCostFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }



        @Override
        public JSONResult<Long> applyRefundRebate(@RequestBody ApplyRefundRebateReq req) {
            return fallBackError("餐饮公司申请退返款");
        }


        @Override
        public JSONResult<PageBean<RefundRebateListDTO>> list(
                @RequestBody RefundRebatePageParam param) {
            return fallBackError("退返款申请列表");
        }

        @Override
        public JSONResult<List<RefundRebateListDTO>> rebatesignList(
                @RequestBody RefundRebatePageParam param) {
            return fallBackError("关联签约单");
        }


    }


}
