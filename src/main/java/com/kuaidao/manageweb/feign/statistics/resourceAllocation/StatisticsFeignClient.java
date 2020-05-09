package com.kuaidao.manageweb.feign.statistics.resourceAllocation;


import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.stastics.dto.resourceAllocation.ResourceAllocationDto;
import com.kuaidao.stastics.dto.resourceAllocation.ResourceAllocationQueryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "statstics-service", path = "/statstics/resourceAllocation", fallback = StatisticsFeignClient.HystrixClientFallback.class)
public interface StatisticsFeignClient {

    /**
     * 资源分配组
     * @param resourceAllocationQueryDto
     * @return
     */
    @PostMapping("/getResourceAllocationList")
    JSONResult<List<ResourceAllocationDto>> getResourceAllocationList(@RequestBody ResourceAllocationQueryDto resourceAllocationQueryDto);

    /**
     * 资源分配组分页
     * @param
     * @return
     */
    @PostMapping("/getResourceAllocationPage")
    JSONResult<PageBean<ResourceAllocationDto>> getResourceAllocationPage(@RequestBody ResourceAllocationQueryDto resourceAllocationQueryDto);


    /**
     * 资源分配 个人
     * @param resourceAllocationQueryDto
     * @return
     */
    @PostMapping("/getResourceAllocationsPersion")
    JSONResult<List<ResourceAllocationDto>> getResourceAllocationsPersion(@RequestBody ResourceAllocationQueryDto resourceAllocationQueryDto);

    /**
     * 资源分配个人分页
     * @param
     * @return
     */
    @PostMapping("/getResourceAllocationPagePersion")
    JSONResult<PageBean<ResourceAllocationDto>> getResourceAllocationPagePersion(@RequestBody ResourceAllocationQueryDto resourceAllocationQueryDto);

    /**
     * 资源分配 个人 按天
     * @param
     * @return
     */
    @PostMapping("/getResourceAllocationsDayPersion")
    JSONResult<List<ResourceAllocationDto>> getResourceAllocationsDayPersion(@RequestBody ResourceAllocationQueryDto resourceAllocationQueryDto);

    /**
     * 资源分配个人分页 按天
     * @param
     * @return
     */
    @PostMapping("/getResourceAllocationDayPagePersion")
    JSONResult<PageBean<ResourceAllocationDto>> getResourceAllocationDayPagePersion(@RequestBody ResourceAllocationQueryDto resourceAllocationQueryDto);

    /**
     * 合计
     * @param
     * @return
     */
    @PostMapping("/getResourceAllocationCount")
    JSONResult<List<ResourceAllocationDto>> getResourceAllocationCount(@RequestBody ResourceAllocationQueryDto resourceAllocationQueryDto);

    @Component
    class HystrixClientFallback implements StatisticsFeignClient {

        private static Logger logger = LoggerFactory.getLogger(StatisticsFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<List<ResourceAllocationDto>> getResourceAllocationList(ResourceAllocationQueryDto resourceAllocationQueryDto) {
            return fallBackError("查询组分配列表失败");
        }

        @Override
        public JSONResult<PageBean<ResourceAllocationDto>> getResourceAllocationPage(ResourceAllocationQueryDto resourceAllocationQueryDto) {
            return fallBackError("分页查询组分配列表失败");
        }

        @Override
        public JSONResult<List<ResourceAllocationDto>> getResourceAllocationsPersion(ResourceAllocationQueryDto resourceAllocationQueryDto) {
            return fallBackError("查询个人分配列表失败");
        }

        @Override
        public JSONResult<PageBean<ResourceAllocationDto>> getResourceAllocationPagePersion(ResourceAllocationQueryDto resourceAllocationQueryDto) {
            return fallBackError("查询个人分配列表失败");
        }

        @Override
        public JSONResult<List<ResourceAllocationDto>> getResourceAllocationsDayPersion(ResourceAllocationQueryDto resourceAllocationQueryDto) {
            return fallBackError("查询个人按天分配列表失败");
        }

        @Override
        public JSONResult<PageBean<ResourceAllocationDto>> getResourceAllocationDayPagePersion(ResourceAllocationQueryDto resourceAllocationQueryDto) {
            return fallBackError("查询个人按天分配列表失败");
        }

        @Override
        public JSONResult<List<ResourceAllocationDto>> getResourceAllocationCount(ResourceAllocationQueryDto resourceAllocationQueryDto) {
            return fallBackError("查询组合计失败");
        }
    }
}
