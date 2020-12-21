package com.kuaidao.manageweb.controller.preference;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/preference")
public class PreferenceController {

    @RequestMapping("/listPage")
    public String listPage(HttpServletRequest request) {
        return "assignrule/telemarketingPreferences";
    }

}