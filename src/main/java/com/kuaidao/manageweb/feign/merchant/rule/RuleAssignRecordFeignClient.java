package com.kuaidao.manageweb.feign.merchant.rule;

import com.kuaidao.merchant.dto.clue.ResourceStatisticsParamDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.merchant.dto.clue.ResourceStatisticsDto;

/**
 * 查询商家查询分配数
 * 
 * @author: fanjd
 * @date: 2019年09月06日
 * @version V1.0
 */
@FeignClient(name = "merchant-service", path = "/merchant/ruleAssignRecord", fallback = RuleAssignRecordFeignClient.HystrixClientFallback.class)
public interface RuleAssignRecordFeignClient {

    /**
     * 查询商家查询分配数
     *
     * @param idEntity
     * @return
     */
    @PostMapping("/countAssginNum")
    JSONResult<ResourceStatisticsDto> countAssginNum(@RequestBody IdEntityLong idEntity);


    @PostMapping("/countAssginStatistic")
    JSONResult<List<ResourceStatisticsDto>> countAssginStatistic(@RequestBody ResourceStatisticsParamDTO paramDTO);

    @Component
    static class HystrixClientFallback implements RuleAssignRecordFeignClient {

        private static Logger logger = LoggerFactory.getLogger(RuleAssignRecordFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(), SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }



        @Override
        public JSONResult<ResourceStatisticsDto> countAssginNum(IdEntityLong idEntity) {
            return fallBackError("查询商家查询分配数");
        }

        @Override
        public JSONResult<List<ResourceStatisticsDto>> countAssginStatistic(
            ResourceStatisticsParamDTO paramDTO) {
            return fallBackError("查询商家查询分配数-维度分组");
        }
    }


}
