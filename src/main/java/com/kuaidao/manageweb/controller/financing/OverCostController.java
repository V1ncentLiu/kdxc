package com.kuaidao.manageweb.controller.financing;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import freemarker.template.Configuration;

/**
 * 退返款
 * 
 * @author Chen
 * @date 2019年4月10日 下午7:23:08
 * @version V1.0
 */
@RequestMapping("/financing/overCost")
@Controller
public class OverCostController {
    private static Logger logger = LoggerFactory.getLogger(OverCostController.class);

    private Configuration configuration = null;

    public OverCostController() {
        configuration = new Configuration();
        configuration.setDefaultEncoding("utf-8");
    }

    /**
     * 申请页面
     *
     * @return
     */
    @RequestMapping("/overCostApplyPage")
    public String balanceAccountPage(HttpServletRequest request) {

        return "financing/overCostApply";
    }



}
