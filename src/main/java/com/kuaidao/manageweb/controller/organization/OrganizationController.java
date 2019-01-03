package com.kuaidao.manageweb.controller.organization;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;

/**
 * 组织机构类
 * @author: Chen Chengxue
 * @date: 2019年1月3日 下午3:04:53   
 * @version V1.0
 */
@Controller
@RequestMapping("/organization/organization")
public class OrganizationController {
    
    /**
     * 组织机构首页
     * @return
     */
    @RequestMapping("/organizationPage")
    public String organizationPage() {
        return "organization/organizationPage";
    }

}
