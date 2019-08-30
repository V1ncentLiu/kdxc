package com.kuaidao.manageweb.controller.statistics.resourcesReport;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: guhuitao
 * @create: 2019-08-22 17:50
 * 资源释放原因
 **/
@Controller
@RequestMapping("/resourceFreed")
public class FreedController {


    /**
     * 资源释放页面
     * @param request
     * @return
     */
    @RequestMapping("/releasePie")
    public String freedView(HttpServletRequest request){
       return "reportResources/releasePie";
    }

}
