package com.kuaidao.manageweb.controller.statistics.performance;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: guhuitao
 * @create: 2019-08-22 18:05
 * 业绩排名
 **/
@Controller
@RequestMapping("/ranking")
public class RankingController {




    /**
     * 事业部业绩排名
     * @param request
     * @return
     */
    @RequestMapping("/deptList")
    public String deptList(HttpServletRequest request){
        return "reportPerformance/deptRankingPerformance";
    }

    /**
     * 电销组业绩排名
     * @param request
     * @return
     */
    @RequestMapping("/groupList")
    public String teamList(HttpServletRequest request){
        return "reportPerformance/groupRankingPerformance";
    }

    /**
     * 电销经理业绩排名
     * @param request
     * @return
     */
    @RequestMapping("/managerList")
    public String managerList(HttpServletRequest request){
        return "reportPerformance/managerRankingPerformance";
    }

}
