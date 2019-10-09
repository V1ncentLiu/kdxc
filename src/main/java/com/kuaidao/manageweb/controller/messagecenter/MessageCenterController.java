package com.kuaidao.manageweb.controller.messagecenter;

import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.feign.announcement.AnnReceiveFeignClient;
import com.kuaidao.manageweb.feign.announcement.BusReceiveFeignClient;
import com.kuaidao.manageweb.feign.messageCenter.MessageCenterFeignClient;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: admin
 * @Date: 2019/1/12 10:17
 * @Description:
 */

@RequestMapping("/messagecenter")
@Controller
public class MessageCenterController {


    private static Logger logger = LoggerFactory.getLogger(MessageCenterController.class);


    @Autowired
    MessageCenterFeignClient messageCenterFeignClient;

    @RequestMapping("/unreadCount")
    @ResponseBody
    public JSONResult<Void> unreadCount(){
        Map map = new HashMap();
        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        map.put("receiveUser",user.getId());
        JSONResult result = messageCenterFeignClient.unreadCount(map);
        return result;
    }

    @RequestMapping("/messageCenter")
    public String listPage(){
        logger.info("--------------------------------------跳转到消息中心页面-----------------------------------------------");
        return "messageCenter/messageCenter";
    }

}
