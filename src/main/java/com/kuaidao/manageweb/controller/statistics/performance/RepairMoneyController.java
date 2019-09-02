package com.kuaidao.manageweb.controller.statistics.performance;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * 补齐尾款
 */
@Controller
@RequestMapping("/repairMoney")
public class RepairMoneyController {


    @RequestMapping("/repairMoneyRepetitionTable")
    public String repairMoneyRepetition(HttpServletRequest request){
        return "reportPerformance/repairMoneyRepetitionTable";
    }

    @RequestMapping("/repairMoneyRepetitionTableTeam")
    public String selfVisitFollowTableTeam(HttpServletRequest request){
        return "reportPerformance/repairMoneyRepetitionTableTeam";
    }


}
