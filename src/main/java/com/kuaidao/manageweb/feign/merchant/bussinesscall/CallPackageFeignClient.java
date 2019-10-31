package com.kuaidao.manageweb.feign.merchant.bussinesscall;

import com.kuaidao.account.dto.call.*;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.merchant.dto.charge.MerchantClueChargeDTO;
import com.kuaidao.merchant.dto.charge.MerchantClueChargePageParam;
import com.kuaidao.merchant.dto.charge.MerchantClueChargeReq;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资源资费
 *
 * @version V1.0
 * @author: zxy
 * @date: 2019年1月4日
 */
@FeignClient(name = "account-service", path = "/account/call/package",
        fallbackFactory = CallPackageFeignClient.HystrixClientFallback.class)
public interface CallPackageFeignClient {

    /**
     * 购买
     * @param callBuyPackageReq
     * @return
     */
    @RequestMapping("/buy")
    JSONResult<CallBuyPackageBuyRes> buy(@RequestBody CallBuyPackageReq callBuyPackageReq);

    /**
     * 变更
     * @param callChangePackageReq
     * @return
     */
    @RequestMapping("/change")
    JSONResult<CallBuyPackageChangeRes> change(@RequestBody CallChangePackageReq callChangePackageReq);

    /**
     * 购买套餐用户余额
     *
     * @return
     */
    @PostMapping("/user/account")
    JSONResult<CallUserAccountRes> getUserAccount(@RequestParam("userId") Long userId);


    /**
     * @param userId
     * @return
     */
    @PostMapping("/hasBuyPackage")
    JSONResult<Boolean> hasBuyPackage(@RequestParam("userId") Long userId);
    /**
     * 套餐列表
     *
     * @return
     */
    @PostMapping("/list")
    JSONResult<CallBuyPackageRes> list( @RequestParam("userId") Long userId);


    /**
     * 用户购买套餐
     * @param userId
     * @return
     */
    @GetMapping("/user/callBuyPackage")
    JSONResult<CallBuyPackageModel> getCallBuyPackage(@RequestParam("userId") Long userId);


    @Component
    class HystrixClientFallback implements FallbackFactory<CallPackageFeignClient> {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        @Override
        public CallPackageFeignClient create(Throwable cause) {
            return new CallPackageFeignClient() {
                @Override
                public JSONResult<CallBuyPackageBuyRes> buy(CallBuyPackageReq callBuyPackageReq) {
                    return fallBackError("购买套餐");
                }

                @Override
                public JSONResult<CallBuyPackageChangeRes> change(CallChangePackageReq callChangePackageReq) {
                    return fallBackError("变更套餐");
                }

                @Override
                public JSONResult<CallUserAccountRes> getUserAccount(Long userId) {
                    return fallBackError("账户余额");
                }

                @Override
                public JSONResult<Boolean> hasBuyPackage(Long userId) {
                    return fallBackError("是否已经购买套餐");
                }

                @Override
                public JSONResult<CallBuyPackageRes> list(Long userId) {
                    return fallBackError("套餐列表");
                }

                @Override
                public JSONResult<CallBuyPackageModel> getCallBuyPackage(Long userId) {
                    return fallBackError("用户购买套餐");
                }

                @SuppressWarnings("rawtypes")
                private JSONResult fallBackError(String name) {
                    logger.error("接口调用失败");
                    logger.error("接口名{}", name);
                    logger.error("失败原因{}", cause);
                    return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                            SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
                }
            };
        }


    }

}
