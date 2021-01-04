package com.kuaidao.manageweb.feign.rule;

import com.kuaidao.businessconfig.dto.rule.ClueAssignRuleDTO;
import com.kuaidao.businessconfig.dto.rule.ClueAssignRulePageParam;
import com.kuaidao.businessconfig.dto.rule.ClueAssignRuleReq;
import com.kuaidao.businessconfig.dto.rule.UserScoreRuleDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 资源分配规则
 * 
 * @author:
 * @date:
 * @version V1.0
 */
@FeignClient(name = "business-config-service", path = "/businessConfig/userStoreRule",
        fallback = UserScoreRuleFeignClient.HystrixClientFallback.class)
public interface UserScoreRuleFeignClient {
    /**
     * 新增资源分配规则
     * 
     * @param
     * @return
     */
    @PostMapping("/create")
    public JSONResult<Long> create(@RequestBody UserScoreRuleDTO clueAssignRuleReq);


    /**
     * 校验规则
     *
     * @param
     * @return
     */
    @PostMapping("/getRuleByBusinessLine")
    public JSONResult<Long> getRuleByBusinessLine(@RequestBody UserScoreRuleDTO clueAssignRuleReq);
    /**
     * 删除资源分配规则
     *
     * @param
     * @return
     */
    @PostMapping("/delete")
    public JSONResult delete(@RequestBody IdListLongReq idList);

    /**
     * 规则详情
     *
     * @param
     * @return
     */
    @PostMapping("/getRuleDetailById")
    public JSONResult getRuleDetailById(@RequestBody UserScoreRuleDTO clueAssignRuleReq);

    /**
     * 查询资源分配规则集合
     *
     * @param
     * @return
     */
    @PostMapping("/getUserScoreRuleListByPage")
    public JSONResult<PageBean<UserScoreRuleDTO>> getUserScoreRuleListByPage(
            @RequestBody UserScoreRuleDTO pageParam);


    @Component
    static class HystrixClientFallback implements UserScoreRuleFeignClient {

        private static Logger logger = LoggerFactory.getLogger(UserScoreRuleFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult<Long> create(UserScoreRuleDTO pageParam) {
            return fallBackError("创建顾问得分");
        }

        @Override
        public JSONResult<Long> getRuleByBusinessLine(UserScoreRuleDTO clueAssignRuleReq) {
            return fallBackError("校验规则是否有效");
        }

        @Override
        public JSONResult delete(IdListLongReq idList) {
            return fallBackError("删除失败");
        }

        @Override
        public JSONResult getRuleDetailById(UserScoreRuleDTO clueAssignRuleRe) {
            return fallBackError("顾问得分详情");
        }

        @Override
        public JSONResult<PageBean<UserScoreRuleDTO>> getUserScoreRuleListByPage(UserScoreRuleDTO pageParam) {
            return fallBackError("查询顾问得分规则列表");
        }


    }


}
