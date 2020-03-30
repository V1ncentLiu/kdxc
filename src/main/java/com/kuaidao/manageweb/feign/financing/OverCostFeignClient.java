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
@FeignClient(name = "aggregation-service", path = "/aggregation/financing/overCost", fallback = OverCostFeignClient.HystrixClientFallback.class)
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
    @PostMapping("/overCostConfirmList")
    JSONResult<PageBean<FinanceOverCostRespDto>> overCostConfirmList(@RequestBody FinanceOverCostReqDto reqDto);
    
    /**
     * 超成本申请
     *
     * @param
     * @return
     */
    @PostMapping("/apply")
    JSONResult<String> apply(@RequestBody FinanceOverCostReqDto reqDto);
    
    /**
     * 超成本变为已结算
     *
     * @param
     * @return
     */
    @PostMapping("/settlementOverCost")
    JSONResult<String> settlementOverCost(@RequestBody FinanceOverCostReqDto reqDto);

    /**
     * 根据id查询超成本信息
     *
     * @param
     * @return
     */
    @PostMapping("/findFinanceOverCostById")
    JSONResult<FinanceOverCostRespDto> findFinanceOverCostById(@RequestBody FinanceOverCostReqDto reqDto);
    
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



		@Override
		public JSONResult<String> apply(FinanceOverCostReqDto reqDto) {
			return fallBackError("超成本申请");
		}



		@Override
		public JSONResult<String> settlementOverCost(FinanceOverCostReqDto reqDto) {
			return fallBackError("超成本申请变为已结算");
		}



		@Override
		public JSONResult<FinanceOverCostRespDto> findFinanceOverCostById(FinanceOverCostReqDto reqDto) {
			return fallBackError("根据id查询超成本信息");
		}


    }


}
