package com.kuaidao.manageweb.controller.homepage;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.kuaidao.sys.dto.module.IndexModuleDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;

@Controller
@RequestMapping("/homePage")
public class HomePageController {


    @Value("${ws_url_http}")
    private String wsUrlHttp;
    @Value("${spring.rabbitmq.username}")
    private String mqUserName;
    @Value("${spring.rabbitmq.password}")
    private String mqPassword;
    @Value("${ws_url_https}")
    private String wsUrlHttps;

    /**
     * 首页 跳转
     * @return
     */
    @RequestMapping("/index")
    public String index(HttpServletRequest request) {
        
        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        request.setAttribute("user", user);
        List<IndexModuleDTO> menuList = user.getMenuList();
        request.setAttribute("menuList",menuList);
        request.setAttribute("wsUrlHttp", wsUrlHttp);
        request.setAttribute("wsUrlHttps", wsUrlHttps);
        request.setAttribute("mqUserName", mqUserName);
        request.setAttribute("mqPassword", mqPassword);
        request.setAttribute("userId", user.getId());
       return "index";
    }

}
