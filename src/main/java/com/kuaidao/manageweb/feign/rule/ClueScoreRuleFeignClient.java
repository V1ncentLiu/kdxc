package com.kuaidao.manageweb.feign.rule;

import com.kuaidao.businessconfig.dto.rule.ClueScoreRuleDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 资源分配规则
 * 
 * @author:
 * @date:
 * @version V1.0
 */
@FeignClient(name = "business-config-service", path = "/businessConfig/clueScoreRule",
        fallback = ClueScoreRuleFeignClient.HystrixClientFallback.class)
public interface ClueScoreRuleFeignClient {
    /**
     * 新增资源分配规则
     * 
     * @param
     * @return
     */
    @PostMapping("/create")
    public JSONResult<Long> create(@RequestBody ClueScoreRuleDTO clueScoreRuleDTO);


    /**
     * 校验规则
     *
     * @param
     * @return
     */
    @PostMapping("/getRuleByBusinessLine")
    public JSONResult<Boolean> getRuleByBusinessLine(@RequestBody ClueScoreRuleDTO clueScoreRuleDTO);
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
    public JSONResult getRuleDetailById(@RequestBody ClueScoreRuleDTO clueScoreRuleDTO);

    /**
     * 查询资源分配规则集合
     *
     * @param
     * @return
     */
    @PostMapping("/getUserScoreRuleListByPage")
    public JSONResult<PageBean<ClueScoreRuleDTO>> getClueScoreRuleListByPage(
            @RequestBody ClueScoreRuleDTO clueScoreRuleDTO);

    /**
     * 修改规则状态
     *
     * @param
     * @return
     */
    @PostMapping("/updateStatus")
    public JSONResult updateStatus(@RequestBody ClueScoreRuleDTO clueScoreRuleDTO);

    /**
     * 修改规则状态
     *
     * @param
     * @return
     */
    @PostMapping("/update")
    public JSONResult update(@RequestBody ClueScoreRuleDTO clueScoreRuleDTO);

    @Component
    static class HystrixClientFallback implements ClueScoreRuleFeignClient {

        private static Logger logger = LoggerFactory.getLogger(ClueScoreRuleFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult<Long> create(ClueScoreRuleDTO clueScoreRuleDTO) {
            return fallBackError("创建资源得分");
        }

        @Override
        public JSONResult<Boolean> getRuleByBusinessLine(ClueScoreRuleDTO clueScoreRuleDTO) {
            return fallBackError("校验规则是否有效");
        }

        @Override
        public JSONResult delete(IdListLongReq idList) {
            return fallBackError("删除失败");
        }

        @Override
        public JSONResult getRuleDetailById(ClueScoreRuleDTO clueScoreRuleDTO) {
            return fallBackError("资源得分详情");
        }

        @Override
        public JSONResult<PageBean<ClueScoreRuleDTO>> getClueScoreRuleListByPage(ClueScoreRuleDTO clueScoreRuleDTO) {
            return fallBackError("查询资源得分规则列表");
        }

        @Override
        public JSONResult updateStatus(ClueScoreRuleDTO clueScoreRuleDTO) {
            return fallBackError("资源得分状态修改");
        }

        @Override
        public JSONResult update(ClueScoreRuleDTO clueScoreRuleDTO) {
            return fallBackError("资源得分信息修改");
        }


    }


}
