package com.kuaidao.manageweb.controller.statistics.resourcesReport;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: guhuitao
 * @create: 2019-08-30 16:59
 * 资源释放统计表
 **/
@Controller
@RequestMapping("/freedReport")
public class FreedReportController {


    /**
     * 事业部统计
     * @param request
     * @return
     */
    @RequestMapping("/deptList")
    public String deptList(HttpServletRequest request){
        return "reportResources/resourceFreedDept";
    }

    /**
     * 电销组统计
     * @param request
     * @return
     */
    @RequestMapping("/groupList")
    public String teamList(HttpServletRequest request){
        return "reportResources/resourceFreedGroup";
    }

    /**
     * 电销顾问统计
     * @param request
     * @return
     */
    @RequestMapping("/managerList")
    public String managerList(HttpServletRequest request){
        return "reportResources/resourceFreedManager";
    }




}
