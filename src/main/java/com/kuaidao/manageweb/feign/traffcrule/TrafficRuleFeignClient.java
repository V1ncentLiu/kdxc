package com.kuaidao.manageweb.feign.traffcrule;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.businessconfig.dto.traffcrule.TrafficAssignRuleDTO;
import com.kuaidao.businessconfig.dto.traffcrule.TrafficAssignRulePageParam;
import com.kuaidao.businessconfig.dto.traffcrule.TrafficAssignRuleReq;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;

/**
 * 话务分配规则
 * 
 * @author: zxy
 * @date: 2019年3月14日
 * @version V1.0
 */
@FeignClient(name = "aggregation-service", path = "/aggregation/trafficAssignRule",
        fallback = TrafficRuleFeignClient.HystrixClientFallback.class)
public interface TrafficRuleFeignClient {
    /**
     * 新增话务分配规则
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/create")
    public JSONResult<Long> create(@RequestBody TrafficAssignRuleReq trafficAssignRuleReq);

    /**
     * 修改话务分配规则信息
     * 
     * @param idEntity
     * @return
     */
    @PostMapping("/update")
    public JSONResult update(@RequestBody TrafficAssignRuleReq trafficAssignRuleReq);

    /**
     * 修改话务分配规则状态
     * 
     * @param idEntity
     * @return
     */
    @PostMapping("/updateStatus")
    public JSONResult updateStatus(@RequestBody TrafficAssignRuleReq trafficAssignRuleReq);

    /**
     * 删除话务分配规则
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/delete")
    public JSONResult delete(@RequestBody IdListLongReq idList);


    /**
     * 根据id查询话务分配规则信息
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/get")
    public JSONResult<TrafficAssignRuleDTO> get(@RequestBody IdEntityLong id);


    /**
     * 查询话务分配规则集合
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/list")
    public JSONResult<PageBean<TrafficAssignRuleDTO>> list(
            @RequestBody TrafficAssignRulePageParam pageParam);

    /**
     * 查询话务分配规则集合（不分页）
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/listNoPage")
    public JSONResult<List<TrafficAssignRuleDTO>> listNoPage(
            @RequestBody TrafficAssignRulePageParam pageParam);



    @Component
    static class HystrixClientFallback implements TrafficRuleFeignClient {

        private static Logger logger = LoggerFactory.getLogger(TrafficRuleFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<TrafficAssignRuleDTO> get(IdEntityLong id) {
            return fallBackError("根据id查询话务分配规则信息");
        }

        @Override
        public JSONResult update(TrafficAssignRuleReq trafficAssignRuleReq) {
            return fallBackError("修改话务分配规则信息");
        }

        @Override
        public JSONResult updateStatus(TrafficAssignRuleReq trafficAssignRuleReq) {
            return fallBackError("修改话务分配规则状态");
        }

        @Override
        public JSONResult<Long> create(TrafficAssignRuleReq trafficAssignRuleReq) {
            return fallBackError("新增话务分配规则");
        }

        @Override
        public JSONResult delete(IdListLongReq idList) {
            return fallBackError("删除话务分配规则");
        }


        @Override
        public JSONResult<PageBean<TrafficAssignRuleDTO>> list(
                TrafficAssignRulePageParam pageParam) {
            return fallBackError("查询话务分配规则集合");
        }

        @Override
        public JSONResult<List<TrafficAssignRuleDTO>> listNoPage(
                TrafficAssignRulePageParam pageParam) {
            return fallBackError("查询话务分配规则集合");
        }



    }


}
