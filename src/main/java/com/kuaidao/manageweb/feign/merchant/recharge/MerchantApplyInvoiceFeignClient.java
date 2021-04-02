package com.kuaidao.manageweb.feign.merchant.recharge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.kuaidao.account.dto.recharge.MerchantApplyInvoiceDTO;
import com.kuaidao.account.dto.recharge.MerchantApplyInvoiceReq;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;

import feign.hystrix.FallbackFactory;

/**
 * Created on: 2019-09-23-17:39
 */
@FeignClient(name = "account-service", path = "/account/merchantApplyInvoice",
        fallbackFactory = MerchantApplyInvoiceFeignClient.HystrixClientFallback.class)
public interface MerchantApplyInvoiceFeignClient {
    /**
     * 查询管理端发票申请列表
     * @param req
     * @return
     */
    @PostMapping("/findMerchantApplyInvoiceList")
    JSONResult<PageBean<MerchantApplyInvoiceDTO>> findMerchantApplyInvoiceList(@RequestBody MerchantApplyInvoiceReq req);
    /***
     * 管理端查询发票详情
     * @return
     */
    @PostMapping("/findMerchantApplyInvoiceDeatil")
    JSONResult<MerchantApplyInvoiceDTO> findMerchantApplyInvoiceDeatil(@RequestBody IdEntityLong req);

    /**
     * 更新发票申请记录
     * @param req
     * @return
     */
    @PostMapping("/updateApplyInvoice")
    public JSONResult updateApplyInvoice(@RequestBody MerchantApplyInvoiceReq req);

    @Component
    static class HystrixClientFallback implements FallbackFactory<MerchantApplyInvoiceFeignClient> {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        @Override
        public MerchantApplyInvoiceFeignClient create(Throwable cause) {
            return new MerchantApplyInvoiceFeignClient() {
                @SuppressWarnings("rawtypes")
                private JSONResult fallBackError(String name) {
                    logger.error("接口调用失败");
                    logger.error("接口名{}", name);
                    logger.error("失败原因{}", cause);
                    return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(), SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
                }

                @Override
                public JSONResult<PageBean<MerchantApplyInvoiceDTO>> findMerchantApplyInvoiceList(@RequestBody MerchantApplyInvoiceReq req) {
                    return fallBackError("查询管理端发票申请列表");
                }

                @Override
                public JSONResult updateApplyInvoice(@RequestBody MerchantApplyInvoiceReq req) {
                    return fallBackError("更新发票申请记录");
                }

                @Override
                public JSONResult findMerchantApplyInvoiceDeatil(@RequestBody IdEntityLong req) {
                    return fallBackError("查看发票详情");
                }
            };
        }

    }

}
