package com.kuaidao.manageweb.controller.announcement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;

/**
 * @Auther: admin
 * @Date: 2019/1/2 15:14
 * @Description:
 *      y邀约区域
 */

@Controller
@RequestMapping("/invitearea")
public class InviteAreaController {

    private static Logger logger = LoggerFactory.getLogger(InviteAreaController.class);

    /**
     * 邀约记录列表
     * 
     * @return
     */
    @RequestMapping("/inviteAreaList")
    public String inviteAreaList(HttpServletRequest request) {
        return "inviteArea/inviteAreaList";
    }
    
    /**
     * 添加邀约区域
     * 
     * @return
     */
    @RequestMapping("/addInviteArea")
    public String addInviteArea(HttpServletRequest request) {
        return "inviteArea/addInviteArea";
    }
}
