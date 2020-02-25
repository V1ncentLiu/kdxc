package com.kuaidao.manageweb.feign.rule;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.businessconfig.dto.rule.RuleReportDTO;
import com.kuaidao.businessconfig.dto.rule.RuleReportPageParam;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;

/**
 * 资源分配规则
 * 
 * @author: zxy
 * @date: 2019年3月14日
 * @version V1.0
 */
@FeignClient(name = "business-config-service", path = "/businessConfig/clueAssignRule/ruleReport",
        fallback = RuleReportFeignClient.HystrixClientFallback.class)
public interface RuleReportFeignClient {

    /**
     * 查询资源分配规则集合
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/list")
    public JSONResult<PageBean<RuleReportDTO>> list(@RequestBody RuleReportPageParam pageParam);

    /**
     * 查询资源分配规则集合（不分页）
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/listNoPage")
    public JSONResult<List<RuleReportDTO>> listNoPage(@RequestBody RuleReportPageParam pageParam);



    @Component
    static class HystrixClientFallback implements RuleReportFeignClient {

        private static Logger logger = LoggerFactory.getLogger(RuleReportFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult<PageBean<RuleReportDTO>> list(RuleReportPageParam pageParam) {
            return fallBackError("查询规则报表集合");
        }

        @Override
        public JSONResult<List<RuleReportDTO>> listNoPage(RuleReportPageParam pageParam) {
            return fallBackError("查询规则报表（不分页）集合");
        }



    }


}
