package com.kuaidao.manageweb.controller.clue;

import com.kuaidao.manageweb.feign.clue.TelCreatePhoneAuditFeignClient;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.kuaidao.aggregation.dto.clue.TelCreatePhoneAuditReqDTO;
import com.kuaidao.common.entity.JSONResult;

import java.util.Date;

/**
 * 　　┏┓　　　┏┓+ +
 * 　┏┛┻━━━┛┻┓ + +
 * 　┃　　　　　　　┃
 * 　┃　　　━　　　┃ ++ + + +
 * ████━████ ┃+
 * 　┃　　　　　　　┃ +
 * 　┃　　　┻　　　┃
 * 　┃　　　　　　　┃ + +
 * 　┗━┓　　　┏━┛
 * 　　　┃　　　┃
 * 　　　┃　　　┃ + + + +
 * 　　　┃　　　┃
 * 　　　┃　　　┃ +  神兽保佑
 * 　　　┃　　　┃    代码无bug
 * 　　　┃　　　┃　　+
 * 　　　┃　 　　┗━━━┓ + +
 * 　　　┃ 　　　　　　　┣┓
 * 　　　┃ 　　　　　　　┏┛
 * 　　　┗┓┓┏━┳┓┏┛ + + + +
 * 　　　　┃┫┫　┃┫┫
 * 　　　　┗┻┛　┗┻┛+ + + +
 *
 * @description: 电销创建手机号审核相关
 * @author: fanjd
 * @create: 2020-07-15 16:17
 */
@Controller
@RequestMapping(value = "/telCreatePhoneAudit")
public class TelCreatePhoneAuditController {
    @Autowired
    private TelCreatePhoneAuditFeignClient telCreatePhoneAuditFeignClient;

    /**
     * 电销手机号审核插入
     */
    @ResponseBody
    @PostMapping("/insert")
    public JSONResult<String> insert(@RequestBody TelCreatePhoneAuditReqDTO reqDTO) {
        UserInfoDTO user = getUser();
        reqDTO.setCreateUser(user.getId());
        reqDTO.setCreateTime(new Date());
        JSONResult<String> jsonResult = telCreatePhoneAuditFeignClient.insert(reqDTO);
        return jsonResult;
    }
    /**
     * 获取当前登录账号
     * @param
     * @return
     */
    private UserInfoDTO getUser() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user;
    }
}
