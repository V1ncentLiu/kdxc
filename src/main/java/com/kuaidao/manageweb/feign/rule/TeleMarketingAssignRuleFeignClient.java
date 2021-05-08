package com.kuaidao.manageweb.feign.rule;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 资源分配规则
 * @author: zxy
 * @date: 2019年3月14日
 * @version V1.0
 */
@FeignClient(name = "business-config-service", path = "/businessConfig/assignrule/telemarketingAssignRule",
        fallback = TeleMarketingAssignRuleFeignClient.HystrixClientFallback.class)
public interface TeleMarketingAssignRuleFeignClient {

    /**
     * 根据成员id删除分配规则
     * @param idEntityLong
     * @return
     */
    @PostMapping("/deleteAssignRuleByMemberId")
    JSONResult deleteAssignRuleByMemberId(@RequestBody IdEntityLong idEntityLong);


    @Component
    static class HystrixClientFallback implements TeleMarketingAssignRuleFeignClient {

        private static Logger logger = LoggerFactory.getLogger(TeleMarketingAssignRuleFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(), SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult deleteAssignRuleByMemberId(IdEntityLong idEntityLong) {
            return fallBackError("根据成员id删除分配规则");
        }
    }

}
