package com.kuaidao.manageweb.feign.rule;

import com.kuaidao.businessconfig.dto.rule.ClueAssignRulePageParam;
import com.kuaidao.businessconfig.dto.rule.ClueAutoAssignRuleDTO;
import com.kuaidao.businessconfig.dto.rule.ClueAutoAssignRulePageParam;
import com.kuaidao.businessconfig.dto.rule.ClueAutoAssignRuleReq;
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
 * @author: zxy
 * @date: 2019年3月14日
 * @version V1.0
 */
@FeignClient(name = "business-config-service-jzx", path = "/businessConfig/clueAutoAssignRule",
        fallback = ClueAutoAssignRuleFeignClient.HystrixClientFallback.class)
public interface ClueAutoAssignRuleFeignClient {
    /**
     * 新增资源分配规则
     * 
     * @param
     * @return
     */
    @PostMapping("/create")
    public JSONResult<Long> create(@RequestBody ClueAutoAssignRuleReq clueAssignRuleReq);

    /**
     * 修改资源分配规则信息
     * 
     * @param
     * @return
     */
    @PostMapping("/update")
    public JSONResult update(@RequestBody ClueAutoAssignRuleReq clueAssignRuleReq);

    /**
     * 修改资源分配规则状态
     * 
     * @param
     * @return
     */
    @PostMapping("/updateStatus")
    public JSONResult updateStatus(@RequestBody ClueAutoAssignRuleReq clueAssignRuleReq);

    /**
     * 删除资源分配规则
     * 
     * @param
     * @return
     */
    @PostMapping("/delete")
    public JSONResult delete(@RequestBody IdListLongReq idList);

    /**
     * 复制规则
     * 
     * @param
     * @return
     */
    @PostMapping("/copy")
    public JSONResult copy(@RequestBody ClueAutoAssignRuleReq clueAssignRuleReq);


    /**
     * 根据id查询资源分配规则信息
     * 
     * @param
     * @return
     */
    @PostMapping("/get")
    public JSONResult<ClueAutoAssignRuleDTO> get(@RequestBody IdEntityLong id);


    /**
     * 查询资源分配规则集合
     * 
     * @param
     * @return
     */
    @PostMapping("/list")
    public JSONResult<PageBean<ClueAutoAssignRuleDTO>> list(
            @RequestBody ClueAutoAssignRulePageParam pageParam);

    /**
     * 查询资源分配规则集合（不分页）
     * 
     * @param
     * @return
     */
    @PostMapping("/listNoPage")
    public JSONResult<List<ClueAutoAssignRuleDTO>> listNoPage(
            @RequestBody ClueAutoAssignRulePageParam pageParam);

    /**
     * 有效规则集合
     * 
     * @param
     * @return
     */
    @PostMapping("/allValidRule")
    public JSONResult<List<ClueAutoAssignRuleDTO>> allValidRule(
            @RequestBody ClueAssignRulePageParam pageParam);



    @Component
    static class HystrixClientFallback implements ClueAutoAssignRuleFeignClient {

        private static Logger logger = LoggerFactory.getLogger(ClueAutoAssignRuleFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<ClueAutoAssignRuleDTO> get(IdEntityLong id) {
            return fallBackError("根据id查询资源分配规则信息");
        }

        @Override
        public JSONResult update(ClueAutoAssignRuleReq clueAssignRuleReq) {
            return fallBackError("修改资源分配规则信息");
        }

        @Override
        public JSONResult updateStatus(ClueAutoAssignRuleReq clueAssignRuleReq) {
            return fallBackError("修改资源分配规则状态");
        }

        @Override
        public JSONResult<Long> create(ClueAutoAssignRuleReq clueAssignRuleReq) {
            return fallBackError("新增资源分配规则");
        }

        @Override
        public JSONResult delete(IdListLongReq idList) {
            return fallBackError("删除资源分配规则");
        }

        @Override
        public JSONResult copy(ClueAutoAssignRuleReq ClueAutoAssignRuleReq) {
            return fallBackError("复制规则");
        }


        @Override
        public JSONResult<PageBean<ClueAutoAssignRuleDTO>> list(ClueAutoAssignRulePageParam pageParam) {
            return fallBackError("查询资源分配规则集合");
        }

        @Override
        public JSONResult<List<ClueAutoAssignRuleDTO>> listNoPage(ClueAutoAssignRulePageParam pageParam) {
            return fallBackError("查询资源分配规则集合");
        }

        @Override
        public JSONResult<List<ClueAutoAssignRuleDTO>> allValidRule(ClueAssignRulePageParam pageParam) {
            return fallBackError("有效规则集合");
        }



    }


}
