/**
 * 
 */
package com.kuaidao.manageweb.controller.schedule;

import java.lang.reflect.InvocationTargetException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
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
    @RequiresPermissions("sys:scheduleManager:view")
    public String initScheduleList(HttpServletRequest request) {


        return "schedule/scheduleManagerPage";
    }

    /***
     * 新增定时任务
     * 
     * @return
     */
    @RequestMapping("/initCreateSchedule")
    @RequiresPermissions("sys:scheduleManager:add")
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
    @RequiresPermissions("sys:scheduleManager:edit")
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
    @RequiresPermissions("sys:scheduleManager:view")
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
    @RequiresPermissions(value = {"sys:scheduleManager:edit", "sys:scheduleManager:add"},
            logical = Logical.OR)
    @LogRecord(description = "新增或修改定时任务", operationType = OperationType.INSERT,
            menuName = MenuEnum.SCHEDULE_MANAGEMENT)
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
     * 修改定时任务状态
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/setStatus")
    @ResponseBody
    @RequiresPermissions("sys:scheduleManager:edit")
    @LogRecord(description = "修改定时任务状态", operationType = OperationType.ENABLE,
            menuName = MenuEnum.SCHEDULE_MANAGEMENT)
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

    /**
     * 删除定时任务
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/delete")
    @ResponseBody
    @RequiresPermissions("sys:scheduleManager:delete")
    @LogRecord(description = "删除定时任务", operationType = OperationType.DELETE,
            menuName = MenuEnum.SCHEDULE_MANAGEMENT)
    public JSONResult delete(@Valid @RequestBody JobStatusSetReq jobStatusSetReq,
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
