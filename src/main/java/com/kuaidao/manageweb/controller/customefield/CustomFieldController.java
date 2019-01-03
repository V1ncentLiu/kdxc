package com.kuaidao.manageweb.controller.customefield;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *  自定义字段 
 * @author: Chen Chengxue
 * @date: 2018年12月28日 下午1:45:12   
 * @version V1.0
 */
@Controller
@RequestMapping("/customfield/customField")
public class CustomFieldController {
    
    /***
     *   菜单  首页
     * @return
     */
    @RequestMapping("/customFieldMenuPage")
    public String customFieldMenuPage() {
        return "customfield/customFieldMenuPage";
    }
    
    
    /***
     *  自定义字段 首页
     * @return
     */
    @RequestMapping("/customFieldPage")
    public String customFieldPage() {
        return "customfield/customFieldPage";
    }

}
