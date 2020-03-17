package com.kuaidao.manageweb.feign.financing;

import com.kuaidao.aggregation.dto.financing.FinanceOverCostRespDto;
import com.kuaidao.common.entity.PageBean;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.aggregation.dto.financing.FinanceOverCostReqDto;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import lombok.extern.slf4j.Slf4j;

/**
 * 超成本
 * 
 * @author: Fanjd
 * @param
 * @return:
 * @Date: 2020/3/13 10:22
 * @since: 1.0.0
 **/
@FeignClient(name = "aggregation-service", path = "/aggregation//financing/overCost", fallback = OverCostFeignClient.HystrixClientFallback.class)
public interface OverCostFeignClient {

    /**
     * 超成本申请确认
     * 
     * @param
     * @return
     */
    @PostMapping("/confirm")
    JSONResult<String> confirm(@RequestBody FinanceOverCostReqDto reqDto);

    /**
     * 超成本申请驳回
     *
     * @param
     * @return
     */
    @PostMapping("/reject")
    JSONResult<String> reject(@RequestBody FinanceOverCostReqDto reqDto);

    /**
     * 超成本申请列表
     * @param reqDto
     */
    @PostMapping("/overCostApplyList")
    JSONResult<PageBean<FinanceOverCostRespDto>> overCostApplyList(@RequestBody FinanceOverCostReqDto reqDto);

    /**
     * 超成本申请确认列表
     *
     * @author: Fanjd
     * @param
     * @return:
     * @Date: 2020/3/12 11:37
     * @since: 1.0.0
     **/
    JSONResult<PageBean<FinanceOverCostRespDto>> overCostConfirmList(@RequestBody FinanceOverCostReqDto reqDto);
    @Component
    @Slf4j
    static class HystrixClientFallback implements OverCostFeignClient {


        private JSONResult fallBackError(String name) {
            log.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(), SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }



        @Override
        public JSONResult<String> confirm(@RequestBody FinanceOverCostReqDto reqDto) {
            return fallBackError("超成本申请确认");
        }

        @Override
        public JSONResult<String> reject(@RequestBody FinanceOverCostReqDto reqDto) {
            return fallBackError("超成本申请驳回");
        }

        @Override
        public JSONResult<PageBean<FinanceOverCostRespDto>> overCostApplyList(FinanceOverCostReqDto reqDto) {
            return fallBackError("超成本申请列表");
        }

        @Override
        public JSONResult<PageBean<FinanceOverCostRespDto>> overCostConfirmList(FinanceOverCostReqDto reqDto) {
            return fallBackError("超成本申请列表");
        }


    }


}
