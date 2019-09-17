package com.kuaidao.manageweb.feign.merchant.rule;

import com.kuaidao.manageweb.feign.merchant.clue.ClueManagementFeignClient;
import com.kuaidao.merchant.dto.clue.ClueAssignReqDto;
import com.kuaidao.merchant.dto.clue.ClueManagementDto;
import com.kuaidao.merchant.dto.clue.ClueManagementParamDto;
import com.kuaidao.merchant.dto.clue.ResourceStatisticsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.merchant.dto.rule.MerchantAssignRuleDTO;
import com.kuaidao.merchant.dto.rule.MerchantAssignRulePageParam;
import com.kuaidao.merchant.dto.rule.MerchantAssignRuleReq;
import feign.hystrix.FallbackFactory;
import java.util.List;

/**
 * 查询商家查询分配数
 * 
 * @author: zxy
 * @date: 2019年3月14日
 * @version V1.0
 */
@FeignClient(name = "merchant-service", path = "/merchant/ruleAssignRecord",
        fallbackFactory = MerchantRuleAssignRecordFeignClient.HystrixClientFallback.class)
public interface MerchantRuleAssignRecordFeignClient {
    /**
     * 查询商家查询分配数
     * 
     * @param idEntity
     * @return
     */
    @PostMapping("/countAssginNum")
    JSONResult<ResourceStatisticsDto> countAssginNum(@RequestBody IdEntityLong idEntity, BindingResult result);


    @Component
    static class HystrixClientFallback implements MerchantRuleAssignRecordFeignClient {

        private static Logger logger = LoggerFactory.getLogger(ClueManagementFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(), SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult<ResourceStatisticsDto> countAssginNum(IdEntityLong idEntity, BindingResult result) {
            return fallBackError("查询商家查询分配数");
        }

    }

}
