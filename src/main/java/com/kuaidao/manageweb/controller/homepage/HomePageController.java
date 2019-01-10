package com.kuaidao.manageweb.controller.homepage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/homePage")
public class HomePageController {
    
    /**
     * 首页 跳转
     * @return
     */
    @RequestMapping("/index")
    public String index() {
        return "index";
    }

}
