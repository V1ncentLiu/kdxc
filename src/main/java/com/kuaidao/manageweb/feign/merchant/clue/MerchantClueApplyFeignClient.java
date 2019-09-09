package com.kuaidao.manageweb.feign.merchant.clue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoPageParam;
import com.kuaidao.sys.dto.user.UserInfoReq;

/**
 * 资源
 * 
 * @author: fanjd
 * @date: 2019年09月06日
 * @version V1.0
 */
@FeignClient(name = "merchant-service", path = "", fallback = MerchantClueApplyFeignClient.HystrixClientFallback.class)
public interface MerchantClueApplyFeignClient {


    @Component
    static class HystrixClientFallback implements MerchantClueApplyFeignClient {

        private static Logger logger = LoggerFactory.getLogger(MerchantClueApplyFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(), SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }



    }


}
