package com.kuaidao.manageweb.feign.res;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.kuaidao.aggregation.dto.res.ResQueryDto;
import com.kuaidao.aggregation.dto.res.ResRequirement;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;

/**
 * 请求资源
 */
@FeignClient(name = "aggregation-service", path = "/aggregation/app/requirment",
        fallback = RequirementFeignClient.HystrixClientFallback.class)
public interface RequirementFeignClient {

    /**
     * 分页查询
     * @param dto
     * @return
     */
    @RequestMapping("/querybyPage")
    public JSONResult<PageBean<ResRequirement>> queryPage(@RequestBody ResQueryDto dto);

    /**
     * 查询全部
     * @param dto
     * @return
     */
    @RequestMapping("/querylist")
    public JSONResult<List<ResRequirement>> queryList(@RequestBody ResQueryDto dto);



    @Component
    static class HystrixClientFallback implements RequirementFeignClient {
        private static Logger logger =
                LoggerFactory.getLogger(RequirementFeignClient.HystrixClientFallback.class);

        @SuppressWarnings("rawtypes")
        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<PageBean<ResRequirement>> queryPage(ResQueryDto dto) {
            return fallBackError("资源请求分页查询");
        }

        @Override
        public JSONResult<List<ResRequirement>> queryList(ResQueryDto dto) {
            return fallBackError("资源请求查询导出");
        }
    }



}
