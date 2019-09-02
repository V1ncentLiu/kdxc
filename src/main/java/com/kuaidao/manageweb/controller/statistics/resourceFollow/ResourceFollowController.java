package com.kuaidao.manageweb.controller.statistics.resourceFollow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: guhuitao
 * @create: 2019-08-30 17:10
 * 分配资源跟访时间分布表
 **/
@Controller
@RequestMapping("/resourceFollow")
public class ResourceFollowController {




    /**
     * 事业部统计
     * @param request
     * @return
     */
    @RequestMapping("/deptList")
    public String deptList(HttpServletRequest request){
        return "reportResourceFollow/followDept";
    }

    /**
     * 电销组统计
     * @param request
     * @return
     */
    @RequestMapping("/groupList")
    public String teamList(HttpServletRequest request){
        return "reportResourceFollow/followGroup";
    }

    /**
     * 电销顾问统计
     * @param request
     * @return
     */
    @RequestMapping("/managerList")
    public String managerList(HttpServletRequest request){
        return "reportResourceFollow/followManager";
    }

}
