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

/**
 * 重单
 */
@FeignClient(name="statstics-service",path = "/statstics/dupOrder",fallback = DupOrderClient.HystrixClientFallback.class)
public interface DupOrderClient {

    @RequestMapping("/queryPage")
    public JSONResult<Map<String,Object>> queryByPage(@RequestBody DupOrderQueryDto baseQueryDto);

    @RequestMapping("/queryList")
    public JSONResult<List<DupOrderDto>> queryListByParams(@RequestBody DupOrderQueryDto baseQueryDto);

    @RequestMapping("/queryPageByGroup")
    public JSONResult<Map<String,Object>> queryByPageByGroup(@RequestBody DupOrderQueryDto baseQueryDto);

    @RequestMapping("/queryListByGroup")
    public JSONResult<List<DupOrderDto>> queryListByGroup(@RequestBody DupOrderQueryDto baseQueryDto);


    @RequestMapping("/queryPageBySale")
    public JSONResult<Map<String,Object>> queryByPageBySale(@RequestBody DupOrderQueryDto baseQueryDto);


    @RequestMapping("/queryListBySale")
    public JSONResult<List<DupOrderDto>> queryListBySale(@RequestBody DupOrderQueryDto baseQueryDto);

    @Component
    class HystrixClientFallback implements DupOrderClient {


        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Map<String, Object>> queryByPage(DupOrderQueryDto baseQueryDto) {
            return fallBackError("重单表分页查询");
        }

        @Override
        public JSONResult<List<DupOrderDto>> queryListByParams(DupOrderQueryDto baseQueryDto) {
            return fallBackError("重单表导出excel查询");
        }

        @Override
        public JSONResult<Map<String, Object>> queryByPageByGroup(DupOrderQueryDto baseQueryDto) {
            return fallBackError("电销组重单表分页查询");
        }

        @Override
        public JSONResult<List<DupOrderDto>> queryListByGroup(DupOrderQueryDto baseQueryDto) {
            return fallBackError("电销组重单表分页查询");
        }

        @Override
        public JSONResult<Map<String, Object>> queryByPageBySale(DupOrderQueryDto baseQueryDto) {
            return fallBackError("电销顾问重单表分页查询");
        }

        @Override
        public JSONResult<List<DupOrderDto>> queryListBySale(DupOrderQueryDto baseQueryDto) {
            return fallBackError("电销顾问重单表分页查询");
        }
    }
}

