package com.kuaidao.manageweb.feign.cluerule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.businessconfig.dto.cluerule.ClueReleaseAndReceiveRuleDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IntegerEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.feign.client.ClientFeignClient;


/**
 * 签约记录
 * 
 * @author Chen
 * @date 2019年3月1日 下午6:36:23
 * @version V1.0
 */
@FeignClient(name = "aggregation-service", path = "/aggregation/cluerule/clueRule",
        fallback = ClueRuleFeignClient.HystrixClientFallback.class)
public interface ClueRuleFeignClient {

    @PostMapping("/queryAllClueRule")
    public JSONResult<ClueReleaseAndReceiveRuleDTO> queryAllClueRule(
            @RequestBody IntegerEntity integerEntity);

    @PostMapping("/insertAndUpdateClueRule")
    public JSONResult<Boolean> insertAndUpdateClueRule(
            @RequestBody ClueReleaseAndReceiveRuleDTO reqAndReceiveRuleDTO);

    /**
     * 删除电销规则 -具体人员的规则
     * 
     * @param idEntityLong
     * @return
     */
    @PostMapping("/deleteTeleDirectorRuleById")
    public JSONResult<Boolean> deleteTeleDirectorRuleById(IdEntityLong idEntityLong);

    @Component
    static class HystrixClientFallback implements ClueRuleFeignClient {

        private static Logger logger = LoggerFactory.getLogger(ClientFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<ClueReleaseAndReceiveRuleDTO> queryAllClueRule(
                IntegerEntity integerEntity) {
            return fallBackError("查询所有的规则");
        }

        @Override
        public JSONResult<Boolean> insertAndUpdateClueRule(
                ClueReleaseAndReceiveRuleDTO reqAndReceiveRuleDTO) {
            return fallBackError("插入或更新规则");
        }

        @Override
        public JSONResult<Boolean> deleteTeleDirectorRuleById(IdEntityLong idEntityLong) {
            return fallBackError("删除电销规则");
        }

    }

}

