package com.kuaidao.manageweb.feign.statistics.FirstResourceAllocation;


import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.statistics.resourceAllocation.StatisticsFeignClient;
import com.kuaidao.stastics.dto.firstResourceAllocation.FirstResourceAllocationDto;
import com.kuaidao.stastics.dto.firstResourceAllocation.FirstResourceAllocationQueryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
@FeignClient(name = "statstics-service", path = "/statstics/firstResourceAllocation", fallback = FirstResourceAllocationFeignClient.HystrixClientFallback.class)
public interface FirstResourceAllocationFeignClient {


    /**
     * 查询(不分页) 组
     * @param
     * @return
     */
    @RequestMapping("/getFirstResourceAllocationList")
    JSONResult<List<FirstResourceAllocationDto>> getFirstResourceAllocationList(
            @RequestBody(required=false) FirstResourceAllocationQueryDto firstResourceAllocationQueryDto);

    /**
     * 分页查询 组
     * @return
     */
    @RequestMapping("/getFirstResourceAllocationPage")
    JSONResult<PageBean<FirstResourceAllocationDto>> getFirstResourceAllocationPage(
            @RequestBody(required=false) FirstResourceAllocationQueryDto firstResourceAllocationQueryDto);


    /**
     * 查询(不分页) 个人
     * @param
     * @return
     */
    @RequestMapping("/getFirstResourceAllocationsPersion")
    JSONResult<List<FirstResourceAllocationDto>> getFirstResourceAllocationsPersion(
            @RequestBody(required=false) FirstResourceAllocationQueryDto firstResourceAllocationQueryDto);

    /**
     * 分页查询 个人
     * @return
     */
    @RequestMapping("/getFirstResourceAllocationPagePersion")
    JSONResult<PageBean<FirstResourceAllocationDto>> getFirstResourceAllocationPagePersion(
            @RequestBody(required=false) FirstResourceAllocationQueryDto firstResourceAllocationQueryDto);


    /**
     * 查询(不分页) 个人 按天
     * @param
     * @return
     */
    @RequestMapping("/getFirstResourceAllocationsDayPersion")
    JSONResult<List<FirstResourceAllocationDto>> getFirstResourceAllocationsDayPersion(
            @RequestBody(required=false) FirstResourceAllocationQueryDto firstResourceAllocationQueryDto);

    /**
     * 分页查询 个人 按天
     * @return
     */
    @RequestMapping("/getFirstResourceAllocationDayPagePersion")
    JSONResult<PageBean<FirstResourceAllocationDto>> getFirstResourceAllocationDayPagePersion(
            @RequestBody(required=false) FirstResourceAllocationQueryDto firstResourceAllocationQueryDto);

    @Component
    class HystrixClientFallback implements FirstResourceAllocationFeignClient {

        private static Logger logger = LoggerFactory.getLogger(StatisticsFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult<List<FirstResourceAllocationDto>> getFirstResourceAllocationList(FirstResourceAllocationQueryDto firstResourceAllocationQueryDto) {
            return fallBackError("首次分配查询组失败");
        }

        @Override
        public JSONResult<PageBean<FirstResourceAllocationDto>> getFirstResourceAllocationPage(FirstResourceAllocationQueryDto firstResourceAllocationQueryDto) {
            return fallBackError("首次分配分页查询组失败");
        }

        @Override
        public JSONResult<List<FirstResourceAllocationDto>> getFirstResourceAllocationsPersion(FirstResourceAllocationQueryDto firstResourceAllocationQueryDto) {
            return fallBackError("首次分配查询人失败");
        }

        @Override
        public JSONResult<PageBean<FirstResourceAllocationDto>> getFirstResourceAllocationPagePersion(FirstResourceAllocationQueryDto firstResourceAllocationQueryDto) {
            return fallBackError("首次分配分页查询人失败");
        }

        @Override
        public JSONResult<List<FirstResourceAllocationDto>> getFirstResourceAllocationsDayPersion(FirstResourceAllocationQueryDto firstResourceAllocationQueryDto) {
            return fallBackError("首次分配查询人天失败");
        }

        @Override
        public JSONResult<PageBean<FirstResourceAllocationDto>> getFirstResourceAllocationDayPagePersion(FirstResourceAllocationQueryDto firstResourceAllocationQueryDto) {
            return fallBackError("首次分配分页查询人天失败");
        }
    }


}
