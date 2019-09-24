package com.kuaidao.manageweb.controller.statistics.performance;

import com.kuaidao.manageweb.controller.statistics.BaseStatisticsController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: guhuitao
 * @create: 2019-09-23 17:02
 * 商务报表-业绩报表-来访签约区域表
 **/
@Controller
@RequestMapping("/busVisitSign")
public class BusVisitSignController extends BaseStatisticsController {

    /**
     * 一级页面-按区域统计
     * @return
     */
    @RequestMapping("/areaList")
    public String areaList(HttpServletRequest request){
        initBugOrg(request);
        return "busPerformance/busAreaList";
    }


    /**
     * 二级页面-按集团统计
     * @return
     */
    @RequestMapping("/groupList")
    public String gruopList(HttpServletRequest request){
        initBugOrg(request);
        return "busPerformance/busGroupList";
    }

}
