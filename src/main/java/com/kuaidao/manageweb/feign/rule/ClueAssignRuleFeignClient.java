package com.kuaidao.manageweb.feign.rule;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.aggregation.dto.rule.ClueAssignRuleDTO;
import com.kuaidao.aggregation.dto.rule.ClueAssignRulePageParam;
import com.kuaidao.aggregation.dto.rule.ClueAssignRuleReq;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;

/**
 * 资源分配规则
 * 
 * @author: zxy
 * @date: 2019年3月14日
 * @version V1.0
 */
@FeignClient(name = "aggregation-service", path = "/aggregation/clueAssignRule",
        fallback = ClueAssignRuleFeignClient.HystrixClientFallback.class)
public interface ClueAssignRuleFeignClient {
    /**
     * 新增资源分配规则
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/create")
    public JSONResult<Long> create(@RequestBody ClueAssignRuleReq clueAssignRuleReq);

    /**
     * 修改资源分配规则信息
     * 
     * @param idEntity
     * @return
     */
    @PostMapping("/update")
    public JSONResult update(@RequestBody ClueAssignRuleReq clueAssignRuleReq);

    /**
     * 修改资源分配规则状态
     * 
     * @param idEntity
     * @return
     */
    @PostMapping("/updateStatus")
    public JSONResult updateStatus(@RequestBody ClueAssignRuleReq clueAssignRuleReq);

    /**
     * 删除资源分配规则
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/delete")
    public JSONResult delete(@RequestBody IdListLongReq idList);

    /**
     * 复制规则
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/copy")
    public JSONResult copy(@RequestBody ClueAssignRuleReq clueAssignRuleReq);


    /**
     * 根据id查询资源分配规则信息
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/get")
    public JSONResult<ClueAssignRuleDTO> get(@RequestBody IdEntityLong id);


    /**
     * 查询资源分配规则集合
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/list")
    public JSONResult<PageBean<ClueAssignRuleDTO>> list(
            @RequestBody ClueAssignRulePageParam pageParam);

    /**
     * 查询资源分配规则集合（不分页）
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/listNoPage")
    public JSONResult<List<ClueAssignRuleDTO>> listNoPage(
            @RequestBody ClueAssignRulePageParam pageParam);

    /**
     * 有效规则集合
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/allValidRule")
    public JSONResult<List<ClueAssignRuleDTO>> allValidRule(
            @RequestBody ClueAssignRulePageParam pageParam);



    @Component
    static class HystrixClientFallback implements ClueAssignRuleFeignClient {

        private static Logger logger = LoggerFactory.getLogger(ClueAssignRuleFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<ClueAssignRuleDTO> get(IdEntityLong id) {
            return fallBackError("根据id查询资源分配规则信息");
        }

        @Override
        public JSONResult update(ClueAssignRuleReq clueAssignRuleReq) {
            return fallBackError("修改资源分配规则信息");
        }

        @Override
        public JSONResult updateStatus(ClueAssignRuleReq clueAssignRuleReq) {
            return fallBackError("修改资源分配规则状态");
        }

        @Override
        public JSONResult<Long> create(ClueAssignRuleReq clueAssignRuleReq) {
            return fallBackError("新增资源分配规则");
        }

        @Override
        public JSONResult delete(IdListLongReq idList) {
            return fallBackError("删除资源分配规则");
        }

        @Override
        public JSONResult copy(ClueAssignRuleReq clueAssignRuleReq) {
            return fallBackError("复制规则");
        }


        @Override
        public JSONResult<PageBean<ClueAssignRuleDTO>> list(ClueAssignRulePageParam pageParam) {
            return fallBackError("查询资源分配规则集合");
        }

        @Override
        public JSONResult<List<ClueAssignRuleDTO>> listNoPage(ClueAssignRulePageParam pageParam) {
            return fallBackError("查询资源分配规则集合");
        }

        @Override
        public JSONResult<List<ClueAssignRuleDTO>> allValidRule(ClueAssignRulePageParam pageParam) {
            return fallBackError("有效规则集合");
        }



    }


}
