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
    
    
    /**
     * 电销顾问通话时长表页面
    * @return
     */
    @RequestMapping("/telemarketingCallTable")
    public String telemarketingCallTable() {
        return "reportforms/telemarketingCallTable";
    }
    
    /**
     * 电销顾问通话时长表页面  合计
    * @return
     */
    @RequestMapping("/telemarketingCallTableSum")
    public String telemarketingCallTableSum() {
        return "reportforms/telemarketingCallTableSum";
    }
    
    
    
    /**
     * 电销顾问通话时长表页面  组
    * @return
     */
    @RequestMapping("/telemarketingCallTableTeam")
    public String telemarketingCallTableTeam() {
        return "reportforms/telemarketingCallTableTeam";
    }
    
    
    /**
     * 电销顾问通话时长表页面  个人
    * @return
     */
    @RequestMapping("/telemarketingCallTablePerson")
    public String telemarketingCallTablePerson() {
        return "reportforms/telemarketingCallTablePerson";
    }
    
    
    
    
    
    
    
    
    
    
}
