package com.kuaidao.manageweb.feign.statistics.performance;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.stastics.dto.dupOrder.DupOrderDto;
import com.kuaidao.stastics.dto.dupOrder.DupOrderQueryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@FeignClient(name="statstics-service",path = "/statstics/tailOrder", fallback =TailOrderClient.HystrixClientFallback.class )
public interface TailOrderClient {


    @RequestMapping("/queryPage")
    public JSONResult<Map<String,Object>> queryByPage(@RequestBody DupOrderQueryDto baseQueryDto);

    @RequestMapping("/queryList")
    public JSONResult<List<DupOrderDto>> queryListByParams(@RequestBody DupOrderQueryDto baseQueryDto);

    @RequestMapping("/queryPageBySale")
    public JSONResult<Map<String,Object>> queryByPageBySale(@RequestBody DupOrderQueryDto baseQueryDto);


    @RequestMapping("/queryListBySale")
    public JSONResult<List<DupOrderDto>> queryListBySale(@RequestBody DupOrderQueryDto baseQueryDto);


    @Component
    class HystrixClientFallback implements TailOrderClient {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult<Map<String, Object>> queryByPage(DupOrderQueryDto baseQueryDto) {
            return fallBackError("补尾款重单表分页查询");
        }

        @Override
        public JSONResult<List<DupOrderDto>> queryListByParams(DupOrderQueryDto baseQueryDto) {
            return fallBackError("补尾款重单表导出");
        }

        @Override
        public JSONResult<Map<String, Object>> queryByPageBySale(DupOrderQueryDto baseQueryDto) {
            return fallBackError("补尾款重单表电销组分页查询");
        }

        @Override
        public JSONResult<List<DupOrderDto>> queryListBySale(DupOrderQueryDto baseQueryDto) {
            return fallBackError("补尾款重单表电销组导出");
        }
    }

}
