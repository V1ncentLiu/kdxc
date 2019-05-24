package com.kuaidao.manageweb.controller.statistics.teleSaleTracking;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/statistics/teleStatement")
public class TeleSaleTrackingController {

    /**
     * 电销顾问跟踪表页面
     * @return
     */
    @RequestMapping("/telemarketingFollowTable")
    public String telemarketingFollowTable() {
        return "reportforms/telemarketingFollowTable";
    }


    /**
     * 电销顾问跟踪表页面 合计
     * @return
     */
    @RequestMapping("/telemarketingFollowTableSum")
    public String telemarketingFollowTableSum() {
        return "reportforms/telemarketingFollowTableSum";
    }


    /**
     * 电销顾问跟踪表页面 组
     * @return
     */
    @RequestMapping("/telemarketingFollowTableTeam")
    public String telemarketingFollowTableTeam() {
        return "reportforms/telemarketingFollowTableTeam";
    }

    /**
     * 电销顾问跟踪表页面 个人
     * @return
     */
    @RequestMapping("/telemarketingFollowTablePerson")
    public String telemarketingFollowTablePerson() {
        return "reportforms/telemarketingFollowTablePerson";
    }


}
