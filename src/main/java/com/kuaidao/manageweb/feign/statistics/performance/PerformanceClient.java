package com.kuaidao.manageweb.feign.statistics.performance;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.stastics.dto.base.BaseQueryDto;
import com.kuaidao.stastics.dto.performance.PerformanceDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@FeignClient(name="statstics-service3",path = "/statstics/performance",fallback = PerformanceClient.HystrixClientFallback.class)
public interface PerformanceClient {

    @RequestMapping("/queryPage")
    public JSONResult<Map<String,Object>> queryByPage(@RequestBody BaseQueryDto baseQueryDto);


    @RequestMapping("/queryList")
    public JSONResult<List<PerformanceDto>> queryListByParams(@RequestBody BaseQueryDto baseQueryDto);



    @RequestMapping("/querySalePage")
    public JSONResult<Map<String,Object>> querySalePage(@RequestBody BaseQueryDto baseQueryDto);


    @RequestMapping("/querySaleList")
    public JSONResult<List<PerformanceDto>> querySaleListByParams(@RequestBody BaseQueryDto baseQueryDto);


    @Component
    class HystrixClientFallback implements PerformanceClient {

        private static Logger logger = LoggerFactory.getLogger(PerformanceClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Map<String, Object>> queryByPage(BaseQueryDto baseQueryDto) {
            return fallBackError("组业绩表查询");
        }

        @Override
        public JSONResult<List<PerformanceDto>> queryListByParams(BaseQueryDto baseQueryDto) {
            return fallBackError("组业绩表导出");
        }

        @Override
        public JSONResult<Map<String, Object>> querySalePage(BaseQueryDto baseQueryDto) {
            return fallBackError("顾问业绩表查询");
        }

        @Override
        public JSONResult<List<PerformanceDto>> querySaleListByParams(BaseQueryDto baseQueryDto) {
            return fallBackError("顾问业绩表导出");
        }
    }

}
