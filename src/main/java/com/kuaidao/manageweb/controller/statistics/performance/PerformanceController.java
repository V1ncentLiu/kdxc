package com.kuaidao.manageweb.controller.statistics.performance;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: guhuitao
 * @create: 2019-08-22 17:44
 * 电销业绩报表
 **/
@Controller
@RequestMapping("/performance")
public class PerformanceController {



    /**
     * 电销组业绩
     * @param request
     * @return
     */
    @RequestMapping("/groupList")
    public String teamList(HttpServletRequest request){
        return "reportPerformance/groupPerformance";
    }

    /**
     * 电销经理业绩排名
     * @param request
     * @return
     */
    @RequestMapping("/managerList")
    public String managerList(HttpServletRequest request){
        return "reportPerformance/managerPerformance";
    }

}
