package com.kuaidao.manageweb.feign.changeorg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.aggregation.dto.changeorg.ChangeOrgRecordReqDto;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;



/**
 * 更换组织记录
 * 
 * @author fanjd
 */
@FeignClient(name = "aggregation-service", path = "/aggregation/changeorg", fallback = ChangeOrgFeignClient.HystrixClientFallback.class)
public interface ChangeOrgFeignClient {

    @PostMapping("/insert")
    JSONResult insert(@RequestBody ChangeOrgRecordReqDto reqDto);



    @Component
    class HystrixClientFallback implements ChangeOrgFeignClient {
        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        @Override
        public JSONResult insert(ChangeOrgRecordReqDto reqDto) {
            return fallBackError("添加换组记录失败");
        }

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(), SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }
    }

}

