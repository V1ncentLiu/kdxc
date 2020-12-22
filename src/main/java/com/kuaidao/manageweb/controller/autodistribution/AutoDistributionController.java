package com.kuaidao.manageweb.controller.autodistribution;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/autodistribution")
public class AutoDistributionController {


    @RequestMapping("/page")
    public String pageIndex() {
        log.info("====================跳转列表页面==================");
        return "assignrule/autoDistributionSet";
    }
}
