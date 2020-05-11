package com.kuaidao.manageweb.feign.statistics.teleSaleTracking;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.stastics.dto.teleSaleTracking.TeleSaleTrackingDto;
import com.kuaidao.stastics.dto.teleSaleTracking.TeleSaleTrackingQueryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "statstics-service", path = "/statstics/teleSaleTracking", fallback = TeleSaleTrackingFeignClient.HystrixClientFallback.class)
public interface TeleSaleTrackingFeignClient {

    /**
     *  根据电销组查询集合 分页
     */
    @PostMapping("/getRecordByGroupPage")
    JSONResult<PageBean<TeleSaleTrackingDto>> getRecordByGroupPage(@RequestBody TeleSaleTrackingQueryDto trackingQueryDto);

    /**
     *  根据电销组+级别查询集合 分页
     */
    @PostMapping("/getRecordByGroupLevelPage")
    JSONResult<PageBean<TeleSaleTrackingDto>> getRecordByGroupLevelPage(@RequestBody TeleSaleTrackingQueryDto trackingQueryDto);

    /**
     *  根据电销组+用户查询集合 分页
     */
    @PostMapping("/getRecordByGroupUserIdPage")
    JSONResult<PageBean<TeleSaleTrackingDto>> getRecordByGroupUserIdPage(@RequestBody TeleSaleTrackingQueryDto trackingQueryDto);

    /**
     *  根据电销组+级别+用户查询集合 分页
     */
    @PostMapping("/getRecordByGroupLevelUserIdPage")
    JSONResult<PageBean<TeleSaleTrackingDto>> getRecordByGroupLevelUserIdPage(@RequestBody TeleSaleTrackingQueryDto trackingQueryDto);

    /**
     *  根据电销组+级别+用户+日期 查询集合 分页
     */
    @PostMapping("/getRecordByGroupLevelUserIdDatePage")
    JSONResult<PageBean<TeleSaleTrackingDto>> getRecordByGroupLevelUserIdDatePage(@RequestBody TeleSaleTrackingQueryDto trackingQueryDto);

    /**
     *  根据电销组+用户+日期 查询集合 分页
     */
    @PostMapping("/getRecordByGroupUserIdDatePage")
    JSONResult<PageBean<TeleSaleTrackingDto>> getRecordByGroupUserIdDatePage(@RequestBody TeleSaleTrackingQueryDto trackingQueryDto);

    /**
     *  根据电销组查询集合 不分页
     */
    @PostMapping("/getRecordByGroup")
    JSONResult<List<TeleSaleTrackingDto>> getRecordByGroup(@RequestBody TeleSaleTrackingQueryDto trackingQueryDto);

    /**
     *  根据电销组+级别查询集合 不分页
     */
    @PostMapping("/getRecordByGroupLevel")
    JSONResult<List<TeleSaleTrackingDto>> getRecordByGroupLevel(@RequestBody TeleSaleTrackingQueryDto trackingQueryDto);

    /**
     *  根据电销组+用户查询集合 不分页
     */
    @PostMapping("/getRecordByGroupUserId")
    JSONResult<List<TeleSaleTrackingDto>> getRecordByGroupUserId(@RequestBody TeleSaleTrackingQueryDto trackingQueryDto);

    /**
     *  根据电销组+级别+用户查询集合 不分页
     */
    @PostMapping("/getRecordByGroupLevelUserId")
    JSONResult<List<TeleSaleTrackingDto>> getRecordByGroupLevelUserId(@RequestBody TeleSaleTrackingQueryDto trackingQueryDto);

    /**
     *  根据电销组+级别+用户+日期 查询集合 不分页
     */
    @PostMapping("/getRecordByGroupLevelUserIdDate")
    JSONResult<List<TeleSaleTrackingDto>> getRecordByGroupLevelUserIdDate(@RequestBody TeleSaleTrackingQueryDto trackingQueryDto);

    /**
     *  根据电销组+用户+日期 查询集合 不分页
     */
    @PostMapping("/getRecordByGroupUserIdDate")
    JSONResult<List<TeleSaleTrackingDto>> getRecordByGroupUserIdDate(@RequestBody TeleSaleTrackingQueryDto trackingQueryDto);

    @Component
    class HystrixClientFallback implements TeleSaleTrackingFeignClient {

        private static Logger logger = LoggerFactory.getLogger(TeleSaleTrackingFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<PageBean<TeleSaleTrackingDto>> getRecordByGroupPage(TeleSaleTrackingQueryDto trackingQueryDto) {
            return fallBackError("根据电销组查询集合");
        }

        @Override
        public JSONResult<PageBean<TeleSaleTrackingDto>> getRecordByGroupLevelPage(TeleSaleTrackingQueryDto trackingQueryDto) {
            return fallBackError("根据电销组+级别查询集合");
        }

        @Override
        public JSONResult<PageBean<TeleSaleTrackingDto>> getRecordByGroupUserIdPage(TeleSaleTrackingQueryDto trackingQueryDto) {
            return fallBackError("根据电销组+用户查询集合");
        }

        @Override
        public JSONResult<PageBean<TeleSaleTrackingDto>> getRecordByGroupLevelUserIdPage(TeleSaleTrackingQueryDto trackingQueryDto) {
            return fallBackError("根据电销组+级别+用户查询集合");
        }

        @Override
        public JSONResult<PageBean<TeleSaleTrackingDto>> getRecordByGroupLevelUserIdDatePage(TeleSaleTrackingQueryDto trackingQueryDto) {
            return fallBackError("根据电销组+级别+用户+日期 查询集合");
        }

        @Override
        public JSONResult<PageBean<TeleSaleTrackingDto>> getRecordByGroupUserIdDatePage(TeleSaleTrackingQueryDto trackingQueryDto) {
            return fallBackError("根据电销组+用户+日期 查询集合");
        }

        @Override
        public JSONResult<List<TeleSaleTrackingDto>> getRecordByGroup(TeleSaleTrackingQueryDto trackingQueryDto) {
            return fallBackError("导出根据电销组查询集合");
        }

        @Override
        public JSONResult<List<TeleSaleTrackingDto>> getRecordByGroupLevel(TeleSaleTrackingQueryDto trackingQueryDto) {
            return fallBackError("导出根据电销组+级别查询集合");
        }

        @Override
        public JSONResult<List<TeleSaleTrackingDto>> getRecordByGroupUserId(TeleSaleTrackingQueryDto trackingQueryDto) {
            return fallBackError("导出根据电销组+用户查询集合");
        }

        @Override
        public JSONResult<List<TeleSaleTrackingDto>> getRecordByGroupLevelUserId(TeleSaleTrackingQueryDto trackingQueryDto) {
            return fallBackError("导出根据电销组+级别+用户查询集合");
        }

        @Override
        public JSONResult<List<TeleSaleTrackingDto>> getRecordByGroupLevelUserIdDate(TeleSaleTrackingQueryDto trackingQueryDto) {
            return fallBackError("导出根据电销组+级别+用户+日期 查询集合");
        }

        @Override
        public JSONResult<List<TeleSaleTrackingDto>> getRecordByGroupUserIdDate(TeleSaleTrackingQueryDto trackingQueryDto) {
            return fallBackError("导出根据电销组+用户+日期 查询集合");
        }
    }


}
