package com.kuaidao.manageweb.feign.financing;

import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.aggregation.dto.financing.ReconciliationConfirmDTO;
import com.kuaidao.aggregation.dto.financing.ReconciliationConfirmPageParam;
import com.kuaidao.aggregation.dto.financing.ReconciliationConfirmReq;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;

/**
 * 对账结算确认
 * 
 * @author: zxy
 * @date: 2019年1月4日
 * @version V1.0
 */
@FeignClient(name = "aggregation-service", path = "/aggregation/reconciliationConfirm",
        fallback = ReconciliationConfirmFeignClient.HystrixClientFallback.class)
public interface ReconciliationConfirmFeignClient {

    /**
     * 对账结算确认列表
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/list")
    public JSONResult<PageBean<ReconciliationConfirmDTO>> list(
            @RequestBody ReconciliationConfirmPageParam param);


    /**
     * 对账、结算确认
     * 
     * @param idEntity
     * @return
     */
    @PostMapping("/reconciliationConfirm")
    public JSONResult<Void> reconciliationConfirm(@RequestBody ReconciliationConfirmReq req);

    /**
     * 已结算佣金总计
     * 
     * @param idEntity
     * @return
     */
    @PostMapping("/sumCommissionMoney")
    public JSONResult<BigDecimal> sumCommissionMoney(
            @RequestBody ReconciliationConfirmPageParam param);

    /**
     * 对账结算申请列表
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/applyList")
    public JSONResult<PageBean<ReconciliationConfirmDTO>> applyList(
            @RequestBody ReconciliationConfirmPageParam param);
    /**
     * 驳回
     * 
     * @param idEntity
     * @return
     */
    @PostMapping("/rejectApply")
    public JSONResult<Void> rejectApply(@RequestBody ReconciliationConfirmReq req);
    

    @Component
    static class HystrixClientFallback implements ReconciliationConfirmFeignClient {

        private static Logger logger =
                LoggerFactory.getLogger(ReconciliationConfirmFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }



        @Override
        public JSONResult<Void> reconciliationConfirm(ReconciliationConfirmReq req) {
            return fallBackError("对账、结算确认");
        }

        @Override
        public JSONResult<PageBean<ReconciliationConfirmDTO>> list(
                ReconciliationConfirmPageParam param) {
            return fallBackError("对账结算确认列表");
        }

        @Override
        public JSONResult<BigDecimal> sumCommissionMoney(ReconciliationConfirmPageParam param) {
            return fallBackError("已结算佣金总计");
        }



        @Override
        public JSONResult<PageBean<ReconciliationConfirmDTO>> applyList(
                ReconciliationConfirmPageParam param) {
            return fallBackError("对账申请列表");
        }



		@Override
		public JSONResult<Void> rejectApply(ReconciliationConfirmReq req) {
			// TODO Auto-generated method stub
			return fallBackError("对账驳回");
		}



    }


}