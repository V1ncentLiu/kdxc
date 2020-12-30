package com.kuaidao.manageweb.controller.autodistribution;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/clueacture")
public class ClueActureTimeScoreController {

    @RequestMapping("/page")
    public String listPage(HttpServletRequest request) {
        return "assignrule/cluesActualTimeScore";
    }
}
