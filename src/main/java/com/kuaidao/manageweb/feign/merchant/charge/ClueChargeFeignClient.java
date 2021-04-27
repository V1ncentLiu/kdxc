package com.kuaidao.manageweb.feign.merchant.charge;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.merchant.dto.charge.MerchantClueChargeDTO;
import com.kuaidao.merchant.dto.charge.MerchantClueChargePageParam;
import com.kuaidao.merchant.dto.charge.MerchantClueChargeReq;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 资源资费
 * @author: zxy
 * @date: 2019年1月4日
 * @version V1.0
 */
@FeignClient(name = "merchant-service", path = "/merchant/merchantClueCharge", fallbackFactory = ClueChargeFeignClient.HystrixClientFallback.class)
public interface ClueChargeFeignClient {
    /***
     * 资源资费列表页
     * @return
     */
    @PostMapping("/queryPage")
    JSONResult<PageBean<MerchantClueChargeDTO>> queryPage(@RequestBody MerchantClueChargeReq pageParam);

    /**
     * 查询资源资费集合
     * @param param
     * @return
     */
    @PostMapping("/listNoPage")
    public JSONResult<List<MerchantClueChargeDTO>> listNoPage(@RequestBody MerchantClueChargePageParam param);

    /**
     * 新增或修改资源资费
     * @param req
     * @return
     */
    @PostMapping("/insertOrUpdate")
    public JSONResult<String> insertOrUpdate(@RequestBody MerchantClueChargeReq req);

    /**
     * 删除资源费用
     * @param merchantClueChargeReq
     * @return
     */
    @PostMapping("/delete")
    JSONResult<String> delete(@RequestBody MerchantClueChargeReq merchantClueChargeReq);

    /**
     * 根据ID查询资费信息
     * @param idEntityLong
     * @return
     */
    @PostMapping("/get")
    JSONResult<MerchantClueChargeDTO> get(@RequestBody IdEntityLong idEntityLong);

    @Component
    static class HystrixClientFallback implements FallbackFactory<ClueChargeFeignClient> {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        @Override
        public ClueChargeFeignClient create(Throwable cause) {
            return new ClueChargeFeignClient() {
                @SuppressWarnings("rawtypes")
                private JSONResult fallBackError(String name) {
                    logger.error("接口调用失败");
                    logger.error("接口名{}", name);
                    logger.error("失败原因{}", cause);
                    return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(), SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
                }

                @Override
                public JSONResult<String> insertOrUpdate(@RequestBody MerchantClueChargeReq req) {
                    return fallBackError("新增或修改资源资费");
                }

                @Override
                public JSONResult<List<MerchantClueChargeDTO>> listNoPage(@RequestBody MerchantClueChargePageParam param) {
                    return fallBackError("查询资源资费集合");
                }

                @Override
                public JSONResult<PageBean<MerchantClueChargeDTO>> queryPage(MerchantClueChargeReq pageParam) {
                    return fallBackError("查询资源资费列表");
                }

                @Override
                public JSONResult<String> delete(MerchantClueChargeReq merchantClueChargeReq) {
                    return fallBackError("删除资费");
                }

                @Override
                public JSONResult<MerchantClueChargeDTO> get(IdEntityLong idEntityLong) {
                    return fallBackError("根据ID查询资费信息");
                }
            };
        }

    }

}
