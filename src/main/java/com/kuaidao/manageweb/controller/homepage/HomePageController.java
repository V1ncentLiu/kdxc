package com.kuaidao.manageweb.controller.homepage;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.kuaidao.sys.dto.module.ModuleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import lombok.Data;

@Controller
@RequestMapping("/homePage")
public class HomePageController {
    
    /**
     * 首页 跳转
     * @return
     */
    @RequestMapping("/index")
    public String index(HttpServletRequest request) {
     /*   List<MenuData> list = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            MenuData menuData = new MenuData();
            menuData.setMenuGroup("菜单组-"+i);
            List<Menu> subList = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                Menu menu = new Menu();
                menu.setName("菜单-"+j);
                menu.setUrl("/customfield/customField/customFieldMenuPage");
                
                subList.add(menu);
            }
            menuData.setSubList(subList);
            list.add(menuData);
        }
        
        
       request.setAttribute("menuList",list);*/
        
     /*   Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        request.setAttribute("user", user);
        List<ModuleInfoDTO> menuList = user.getMenuList();*/
       return "index";
    }

}
