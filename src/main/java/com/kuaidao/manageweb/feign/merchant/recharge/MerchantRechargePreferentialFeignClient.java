package com.kuaidao.manageweb.feign.merchant.recharge;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.merchant.dto.recharge.MerchantRechargePreferentialDTO;
import com.kuaidao.merchant.dto.recharge.MerchantRechargePreferentialReq;
import feign.hystrix.FallbackFactory;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created on: 2019-09-23-9:16
 */
@FeignClient(name = "merchant-service", path = "/merchant/merchantRechargePreferential",
    fallbackFactory = MerchantRechargePreferentialFeignClient.HystrixClientFallback.class)
public interface MerchantRechargePreferentialFeignClient {


    /**
     * 批量保存充值优惠
     * @param list
     * @return
     */
    @PostMapping("/saveBatchRechargePreferential")
    public JSONResult saveBatchRechargePreferential(@RequestBody List<MerchantRechargePreferentialDTO> list);

    /**
     * 批量更新充值优惠
     * @param list
     * @return
     */
    @PostMapping("/updateBatchRechargePreferential")
    public JSONResult updateBatchRechargePreferential(@RequestBody List<MerchantRechargePreferentialDTO> list);

    /**
     * 删除充值优惠
     * @param idEntityLong
     * @return
     */
    @PostMapping("/deleteBatchRechargePreferential")
    public JSONResult deleteBatchRechargePreferential(@RequestBody IdEntityLong idEntityLong);

    /**
     * 查询所有优惠金额
     * @param req
     * @return
     */
    @PostMapping("/findAllRechargePreferential")
    public JSONResult<List<MerchantRechargePreferentialDTO>> findAllRechargePreferential(@RequestBody MerchantRechargePreferentialReq req);

    @Component
    static class HystrixClientFallback implements FallbackFactory<MerchantRechargePreferentialFeignClient> {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        @Override
        public MerchantRechargePreferentialFeignClient create(Throwable cause) {
            return new MerchantRechargePreferentialFeignClient() {
                @SuppressWarnings("rawtypes")
                private JSONResult fallBackError(String name) {
                    logger.error("接口调用失败");
                    logger.error("接口名{}", name);
                    logger.error("失败原因{}", cause);
                    return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                        SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
                }

                @Override
                public JSONResult saveBatchRechargePreferential(@RequestBody List<MerchantRechargePreferentialDTO> list) {
                    return fallBackError("批量保存充值优惠");
                }

                @Override
                public JSONResult updateBatchRechargePreferential(@RequestBody List<MerchantRechargePreferentialDTO> list) {
                    return fallBackError("批量更新充值优惠");
                }

                @Override
                public JSONResult deleteBatchRechargePreferential(@RequestBody IdEntityLong idEntityLong) {
                    return fallBackError("删除充值优惠");
                }

                @Override
                public JSONResult<List<MerchantRechargePreferentialDTO>> findAllRechargePreferential(
                    @RequestBody MerchantRechargePreferentialReq req) {
                    return fallBackError("查询优惠金额");
                }
            };
        }



    }

}
