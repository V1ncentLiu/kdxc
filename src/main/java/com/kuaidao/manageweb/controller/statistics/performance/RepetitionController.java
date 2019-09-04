package com.kuaidao.manageweb.controller.statistics.performance;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * 业绩 重单
 */
@Controller
@RequestMapping("/repetition")
public class RepetitionController {


    @RequestMapping("/repetitionTable")
    public String repetitionTable(HttpServletRequest request){
        return "reportPerformance/repetitionTable";
    }

    @RequestMapping("/repetitionTableTeam")
    public String selfVisitFollowTableTeam(HttpServletRequest request){
        return "reportPerformance/repetitionTableTeam";
    }


}
