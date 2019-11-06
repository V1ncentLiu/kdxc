package com.kuaidao.manageweb.entity;

import java.io.Serializable;
import lombok.Data;

@Data
public class LoginReq implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    // 用户名
    private String username;
    // 密码
    private String password;
    // 手机号
    private String phone;
    // 验证码
    private String code;
    // 是否弹出修改密码
    private String isUpdatePassword;
    // 类型 1-短信 2-语音
    private String type;


}
