package com.kuaidao.manageweb.feign.merchant.rule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
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

/**
 * 资源分配规则
 * 
 * @author: zxy
 * @date: 2019年3月14日
 * @version V1.0
 */
@FeignClient(name = "merchant-service", path = "/merchant/merchantAssignRule",
        fallbackFactory = MerchantAssignRuleFeignClient.HystrixClientFallback.class)
public interface MerchantAssignRuleFeignClient {
    /**
     * 新增资源分配规则
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/create")
    public JSONResult<Long> create(@RequestBody MerchantAssignRuleReq merchantAssignRuleReq);

    /**
     * 修改资源分配规则信息
     * 
     * @param idEntity
     * @return
     */
    @PostMapping("/update")
    public JSONResult update(@RequestBody MerchantAssignRuleReq merchantAssignRuleReq);

    /**
     * 修改资源分配规则状态
     * 
     * @param idEntity
     * @return
     */
    @PostMapping("/updateStatus")
    public JSONResult updateStatus(@RequestBody MerchantAssignRuleReq merchantAssignRuleReq);

    /**
     * 删除资源分配规则
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/delete")
    public JSONResult delete(@RequestBody IdListLongReq idList);


    /**
     * 根据id查询资源分配规则信息
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/get")
    public JSONResult<MerchantAssignRuleDTO> get(@RequestBody IdEntityLong id);


    /**
     * 查询资源分配规则集合
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/list")
    public JSONResult<PageBean<MerchantAssignRuleDTO>> list(
            @RequestBody MerchantAssignRulePageParam pageParam);


    @Component
    static class HystrixClientFallback implements FallbackFactory<MerchantAssignRuleFeignClient> {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        @Override
        public MerchantAssignRuleFeignClient create(Throwable cause) {
            return new MerchantAssignRuleFeignClient() {
                @SuppressWarnings("rawtypes")
                private JSONResult fallBackError(String name) {
                    logger.error("接口调用失败");
                    logger.error("接口名{}", name);
                    logger.error("失败原因{}", cause);
                    return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                            SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
                }

                @Override
                public JSONResult<MerchantAssignRuleDTO> get(IdEntityLong id) {
                    return fallBackError("根据id查询资源分配规则信息");
                }

                @Override
                public JSONResult update(MerchantAssignRuleReq merchantAssignRuleReq) {
                    return fallBackError("修改资源分配规则信息");
                }

                @Override
                public JSONResult updateStatus(MerchantAssignRuleReq merchantAssignRuleReq) {
                    return fallBackError("修改资源分配规则状态");
                }

                @Override
                public JSONResult<Long> create(MerchantAssignRuleReq merchantAssignRuleReq) {
                    return fallBackError("新增资源分配规则");
                }

                @Override
                public JSONResult delete(IdListLongReq idList) {
                    return fallBackError("删除资源分配规则");
                }


                @Override
                public JSONResult<PageBean<MerchantAssignRuleDTO>> list(
                        MerchantAssignRulePageParam pageParam) {
                    return fallBackError("查询资源分配规则集合");
                }

            };
        }


    }

}
