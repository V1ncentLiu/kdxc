package com.kuaidao.manageweb.controller.statistics.teleGroupResourceEfficiency;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 电销组资源接通有效率
 */
@Controller
@RequestMapping("/statistics/teleGroupResourceEfficiency")
public class TeleGroupResourceEfficiencyController {


    /**
     *电销组资源接通有效率表
     * @return
     */
    @RequestMapping("/resourceConectTelEfficientTable")
    public String resourceConectTelEfficientTable() {
        return "reportforms/resourceConectTelEfficientTable";
    }


}
