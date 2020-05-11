package com.kuaidao.manageweb.feign.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.schedule.dto.JobCreateReq;
import com.kuaidao.schedule.dto.JobListReq;
import com.kuaidao.schedule.dto.JobStatusSetReq;
import com.kuaidao.schedule.dto.JobUpdateReq;
import com.kuaidao.schedule.entity.QrtzJob;
import feign.hystrix.FallbackFactory;

/**
 * 定时任务
 * 
 * @author: zxy
 * @date: 2019年1月4日
 * @version V1.0
 */
@FeignClient(name = "schedule-service", path = "/schedule/v1.0/job",
        fallbackFactory = ScheduleFeignClient.HystrixClientFallback.class)
public interface ScheduleFeignClient {


    /**
     * 查询定时任务列表
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/listJob")
    public JSONResult<PageBean<QrtzJob>> listJob(@RequestBody JobListReq jobListReq);

    /**
     * 修改定时任务
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/update")
    public JSONResult update(@RequestBody JobUpdateReq jobUpdateReq);

    /**
     * 修改定时任务状态
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/setStatus")
    public JSONResult setStatus(JobStatusSetReq jobStatusSetReq);

    /**
     * 新建定时任务
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/create")
    public JSONResult<String> create(@RequestBody JobCreateReq jobCreateReq);

    /**
     * 查询定时任务详情
     *
     * @param jobId
     * @return
     */
    @PostMapping("/get/{jobId}")
    public JSONResult<QrtzJob> getJob(@PathVariable("jobId") String jobId);


    @Component
    static class HystrixClientFallback implements FallbackFactory<ScheduleFeignClient> {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        @Override
        public ScheduleFeignClient create(Throwable cause) {
            return new ScheduleFeignClient() {
                @SuppressWarnings("rawtypes")
                private JSONResult fallBackError(String name) {
                    logger.error("接口调用失败");
                    logger.error("接口名{}", name);
                    logger.error("失败原因{}", cause);
                    return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                            SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
                }


                @Override
                public JSONResult<PageBean<QrtzJob>> listJob(@RequestBody JobListReq jobListReq) {
                    return fallBackError("查询定时任务列表");
                }

                @Override
                public JSONResult update(@RequestBody JobUpdateReq jobUpdateReq) {
                    return fallBackError("修改定时任务");
                }

                @Override
                public JSONResult setStatus(JobStatusSetReq jobStatusSetReq) {
                    return fallBackError("修改定时任务状态");
                }

                @Override
                public JSONResult<String> create(@RequestBody JobCreateReq jobCreateReq) {
                    return fallBackError("创建定时任务");
                }

                @Override
                public JSONResult<QrtzJob> getJob(String jobId) {
                    return fallBackError("查看定时任务详情");
                }



            };
        }

    }
}
