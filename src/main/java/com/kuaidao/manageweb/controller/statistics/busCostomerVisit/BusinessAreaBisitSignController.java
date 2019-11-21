package com.kuaidao.manageweb.controller.statistics.busCostomerVisit;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description: BusinessAreaBisitSignController
 * @date: 19-11-21 下午3:57
 * @author: xuyunfeng
 * @version: 1.0
 */
@RequestMapping("/businessAreaBisitSign")
@Controller
public class BusinessAreaBisitSignController {

    @RequiresPermissions("businessAreaBisitSign:initBusinessAreaBisitSign:view")
    @RequestMapping("/initBusinessAreaBisitSign")
    public String initBusinessAreaBisitSign(){
        return "reportformsBusiness/businessAreaVisitSign";
    }

}
