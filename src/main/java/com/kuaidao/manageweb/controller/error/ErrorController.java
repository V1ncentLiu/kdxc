/**
 * 
 */
package com.kuaidao.manageweb.controller.error;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author gpc
 *
 */

@Controller
@RequestMapping("/error")
public class ErrorController {

    /***
     * 用户列表页
     * 
     * @return
     */
    @RequestMapping("/error404")
    public String error404(HttpServletRequest request) {
        return "error/404";
    }

    /***
     * 用户列表页
     * 
     * @return
     */
    @RequestMapping("/error403")
    public String error403(HttpServletRequest request) {
        return "error/403";
    }



}
