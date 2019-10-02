package com.kuaidao.manageweb.controller.statistics.resourcesReport;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * 资源报表
 * 自邀约
 */
@Controller
@RequestMapping("/selfVisitFollow")
public class SelfVisitFollowController {

    @RequestMapping("/selfVisitFollowTable")
    public String selfVisitFollowTable(HttpServletRequest request){
        return "reportResources/selfVisitFollowTable";
    }

    @RequestMapping("/selfVisitFollowTableTeam")
    public String selfVisitFollowTableTeam(HttpServletRequest request){
        return "reportResources/selfVisitFollowTableTeam";
    }

}
