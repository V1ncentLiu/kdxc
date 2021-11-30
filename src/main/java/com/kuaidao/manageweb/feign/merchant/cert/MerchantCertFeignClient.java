package com.kuaidao.manageweb.feign.merchant.cert;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.sys.dto.mechant.cert.MerchantCertReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author fengyixuan
 * @version 1.0
 * @description: 证件上传feign
 * @date 2021/7/13 11:23 上午
 */
@FeignClient(name = "sys-service", path = "/sys/merchant/cert",
        fallback = MerchantCertFeignClient.HystrixClientFallback.class)
public interface MerchantCertFeignClient {

    /**
     * @description: 修改证件上传状态
     * @author fengyixuan
     * @date 2021/7/13 11:25 上午
     * @param merchantCertReq
     * @returns com.kuaidao.common.entity.JSONResult<java.lang.String>
    */
    @PostMapping("/updateAuditStatus")
    JSONResult<String> updateAuditStatus(@RequestBody  MerchantCertReq merchantCertReq);

    /**
     * 新增修改商户证件信息
     */
    @PostMapping("/addOrUpdate")
    JSONResult<Boolean> addOrUpdate(@RequestBody MerchantCertReq merchantCertReq) ;

    @Component
    static class HystrixClientFallback implements MerchantCertFeignClient {
        private static Logger logger = LoggerFactory.getLogger(MerchantCertFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }
        @Override
        public JSONResult<String> updateAuditStatus(MerchantCertReq merchantCertReq) {
           return fallBackError("修改证件上传状态");
        }

        @Override
        public JSONResult<Boolean> addOrUpdate(MerchantCertReq merchantCertReq) {
            return fallBackError("修改商户证件信息");
        }
    }
}
