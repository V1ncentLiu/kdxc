package com.kuaidao.manageweb.controller.statistics;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 电销相关报表
 * @author  Devin.Chen
 * @date 2019-05-15 20:51:09
 * @version V1.0
 */
@Controller
@RequestMapping("/statistics/teleStatement")
public class TeleStatementController {

    /**
     * 资源分配页面
    * @return
     */
    @RequestMapping("/resourceAllocation")
    public String resourceAllocationTable() {
        return "/reportforms/resourceAllocationTable";
    }
    
    
    /**
     * 资源分配页面 合计 
    * @return
     */
    @RequestMapping("/resourceAllocationTableSum")
    public String resourceAllocationTableSum() {
        return "/reportforms/resourceAllocationTableSum";
    }
    
    
    /**
     * 资源分配页面  组 
    * @return
     */
    @RequestMapping("/resourceAllocationTableTeam")
    public String resourceAllocationTableTeam() {
        return "/reportforms/resourceAllocationTableTeam";
    }
    
    
    /**
     * 资源分配页面  个人 
    * @return
     */
    @RequestMapping("/resourceAllocationTablePerson")
    public String resourceAllocationTablePerson() {
        return "reportforms/resourceAllocationTablePerson";
    }
    
    
    
    
}
