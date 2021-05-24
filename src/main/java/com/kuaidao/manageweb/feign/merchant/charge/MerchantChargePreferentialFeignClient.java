package com.kuaidao.manageweb.feign.merchant.charge;

import com.kuaidao.account.dto.recharge.MerchantChargePreferentialDto;
import com.kuaidao.account.dto.recharge.MerchantChargePreferentialReq;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 资源资费
 * 
 * @author: zxy
 * @date: 2019年1月4日
 * @version V1.0
 */
@FeignClient(name = "account-service-3", path = "/account/merchantChargePreFerential", fallbackFactory = MerchantChargePreferentialFeignClient.HystrixClientFallback.class)
public interface MerchantChargePreferentialFeignClient {

    /**
     * 查询优惠列表
     */
    @PostMapping("/findPageList")
     JSONResult<PageBean<MerchantChargePreferentialDto>> findPageList(@RequestBody MerchantChargePreferentialReq merchantChargePreferentialReq) ;

    /**
     * 新增修改充值会优惠
     */
    @PostMapping("/addOrUpdate")
     JSONResult<Boolean> addOrUpdate(@RequestBody MerchantChargePreferentialReq merchantChargePreferentialReq) ;

    /**
     * 批量删除充值优惠
     */
    @PostMapping("/batchDel")
     JSONResult<Boolean> batchDel(@RequestBody MerchantChargePreferentialReq  merchantChargePreferentialReq) ;

    /**
     *获取现在优惠
     */
    @PostMapping("/getChargePreferential")
     JSONResult<MerchantChargePreferentialDto> getChargePreferential(@RequestBody IdEntityLong  idEntityLong);

    @Component
    static class HystrixClientFallback implements FallbackFactory<MerchantChargePreferentialFeignClient> {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        @Override
        public MerchantChargePreferentialFeignClient create(Throwable cause) {
            return new MerchantChargePreferentialFeignClient() {

                @SuppressWarnings("rawtypes")
                private JSONResult fallBackError(String name) {
                    logger.error("接口调用失败");
                    logger.error("接口名{}", name);
                    logger.error("失败原因{}", cause);
                    return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(), SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
                }

                @Override
                public JSONResult<PageBean<MerchantChargePreferentialDto>> findPageList(MerchantChargePreferentialReq merchantChargePreferentialReq) {
                    return fallBackError("分页查询服务失败");
                }

                @Override
                public JSONResult<Boolean> addOrUpdate(MerchantChargePreferentialReq merchantChargePreferentialReq) {
                    return fallBackError("新增修改服务失败");
                }

                @Override
                public JSONResult<Boolean> batchDel(MerchantChargePreferentialReq merchantChargePreferentialReq) {
                    return fallBackError("批量删除服务失败");
                }

                @Override
                public JSONResult<MerchantChargePreferentialDto> getChargePreferential(IdEntityLong idEntityLong) {
                    return fallBackError("获取商户当前优惠服务失败");
                }

            };
        }



    }



}
