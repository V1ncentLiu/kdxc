/**
 * 
 */
package com.kuaidao.manageweb.controller.schedule;

import java.lang.reflect.InvocationTargetException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.feign.schedule.ScheduleFeignClient;
import com.kuaidao.schedule.dto.JobCreateReq;
import com.kuaidao.schedule.dto.JobListReq;
import com.kuaidao.schedule.dto.JobStatusSetReq;
import com.kuaidao.schedule.dto.JobUpdateReq;
import com.kuaidao.schedule.entity.QrtzJob;

/**
 * @author gpc
 *
 */

@Controller
@RequestMapping("/schedule/scheduleManager")
public class ScheduleController {
    private static Logger logger = LoggerFactory.getLogger(ScheduleController.class);
    @Autowired
    private ScheduleFeignClient scheduleFeignClient;

    /***
     * 定时任务列表
     * 
     * @return
     */
    @RequestMapping("/initScheduleList")
    public String initScheduleList(HttpServletRequest request) {


        return "schedule/scheduleManagerPage";
    }

    /***
     * 新增定时任务
     * 
     * @return
     */
    @RequestMapping("/initCreateSchedule")
    public String initCreateSchedule(@RequestParam(required = false) String id,
            HttpServletRequest request) {
        // 查询用户信息
        if (id != null) {
            JSONResult<QrtzJob> job = scheduleFeignClient.getJob(id);
            request.setAttribute("schedule", job.getData());
        }
        return "schedule/addSchedulePage";
    }

    /***
     * 编辑定时任务
     * 
     * @return
     */
    @RequestMapping("/initUpdateSchedule")
    public String initUpdateSchedule(@RequestParam String id, HttpServletRequest request) {
        // 查询用户信息
        JSONResult<QrtzJob> job = scheduleFeignClient.getJob(id);
        request.setAttribute("schedule", job.getData());

        return "schedule/editSchedulePage";
    }

    /***
     * 定时任务列表
     * 
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    public JSONResult<PageBean<QrtzJob>> list(@RequestBody JobListReq jobListReq,
            HttpServletRequest request, HttpServletResponse response) {

        JSONResult<PageBean<QrtzJob>> listJob = scheduleFeignClient.listJob(jobListReq);

        return listJob;
    }



    /**
     * 保存定时任务
     * 
     * @param orgDTO
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostMapping("/createOrUpdate")
    @ResponseBody
    public JSONResult create(@Valid @RequestBody JobUpdateReq jobUpdateReq, BindingResult result) {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        if (jobUpdateReq.getJobId() != null) {
            return scheduleFeignClient.update(jobUpdateReq);
        } else {
            JobCreateReq jobCreateReq = new JobCreateReq();
            BeanUtils.copyProperties(jobUpdateReq, jobCreateReq);
            return scheduleFeignClient.create(jobCreateReq);
        }
    }

    /**
     * 修改用户信息
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    public JSONResult update(@Valid @RequestBody JobUpdateReq jobUpdateReq, BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }

        String jobId = jobUpdateReq.getJobId();
        if (jobId == null) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }

        return scheduleFeignClient.update(jobUpdateReq);
    }


    /**
     * 修改定时任务状态
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/setStatus")
    @ResponseBody
    public JSONResult setStatus(@Valid @RequestBody JobStatusSetReq jobStatusSetReq,
            BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }

        String jobId = jobStatusSetReq.getJobId();
        if (jobId == null) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }

        return scheduleFeignClient.setStatus(jobStatusSetReq);
    }


}
