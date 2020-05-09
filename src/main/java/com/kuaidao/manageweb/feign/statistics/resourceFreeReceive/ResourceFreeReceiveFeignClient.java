package com.kuaidao.manageweb.feign.statistics.resourceFreeReceive;


import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.stastics.dto.resourceFreeReceive.ResourceFreeReceiveQueryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@FeignClient(name = "statstics-service", path = "/statstics/resourceFreeReceive", fallback = ResourceFreeReceiveFeignClient.HystrixClientFallback.class)
public interface ResourceFreeReceiveFeignClient {

    /**
     * 组分页
     */
    @RequestMapping("/getGroupPageList")
    JSONResult<Map<String,Object>> getGroupPageList(@RequestBody ResourceFreeReceiveQueryDto resourceFreeReceiveQueryDto);

    /**
     * 组不分页
     */
    @RequestMapping("/getGroupAllList")
    JSONResult<Map<String,Object>> getGroupAllList(@RequestBody ResourceFreeReceiveQueryDto resourceFreeReceiveQueryDto);

    /**
     *
     * 二级页面查询人（不分页）
     */
    @RequestMapping("/getPersonAllList")
    JSONResult<Map<String, Object>> getPersonAllList(ResourceFreeReceiveQueryDto resourceFreeReceiveQueryDto);
    /**
     *
     * 二级页面查询人（分页）
     */
    @RequestMapping("/getPersonPageList")
    JSONResult<Map<String, Object>> getPersonPageList(ResourceFreeReceiveQueryDto resourceFreeReceiveQueryDto);
    /**
     *
     * 三级页面查询人+天（不分页）
     */
    @RequestMapping("/getPersonDayAllList")
    JSONResult<Map<String, Object>> getPersonDayAllList(ResourceFreeReceiveQueryDto resourceFreeReceiveQueryDto);
    /**
     *
     * 三级页面查询人+天（分页）
     */
    @RequestMapping("/getPersonDayPageList")
    JSONResult<Map<String, Object>> getPersonDayPageList(ResourceFreeReceiveQueryDto resourceFreeReceiveQueryDto);

    @Component
    class HystrixClientFallback implements ResourceFreeReceiveFeignClient {

        private static Logger logger = LoggerFactory.getLogger(ResourceFreeReceiveFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Map<String, Object>> getGroupPageList(ResourceFreeReceiveQueryDto resourceFreeReceiveQueryDto) {
            return fallBackError("分页查询组资源释放领取统计表失败");
        }

        @Override
        public JSONResult<Map<String, Object>> getGroupAllList(ResourceFreeReceiveQueryDto resourceFreeReceiveQueryDto) {
            return fallBackError("全部查询组资源释放领取统计表失败");
        }

        @Override
        public JSONResult<Map<String, Object>> getPersonAllList(ResourceFreeReceiveQueryDto resourceFreeReceiveQueryDto) {
            return fallBackError("分页查询人资源释放领取统计表失败");
        }

        @Override
        public JSONResult<Map<String, Object>> getPersonPageList(ResourceFreeReceiveQueryDto resourceFreeReceiveQueryDto) {
            return fallBackError("全部查询人资源释放领取统计表失败");
        }

        @Override
        public JSONResult<Map<String, Object>> getPersonDayAllList(ResourceFreeReceiveQueryDto resourceFreeReceiveQueryDto) {
            return fallBackError("分页查询人天资源释放领取统计表失败");
        }

        @Override
        public JSONResult<Map<String, Object>> getPersonDayPageList(ResourceFreeReceiveQueryDto resourceFreeReceiveQueryDto) {
            return fallBackError("全部查询人天资源释放领取统计表失败");
        }
    }

}
