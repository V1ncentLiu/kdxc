package com.kuaidao.manageweb.constant;

/**
 * manageweb 常量類
 * 
 * @author: Chen Chengxue
 * @date: 2018年12月26日 上午8:56:29
 * @version V1.0
 */
public class Constants {
    // 账户登录状态 --成功
    public static Integer LOGIN_STATUS_SUCCESS = 1;
    // 账户登录状态 --失败，密码或验证码错误
    public static Integer LOGIN_STATUS_PASSWORD_ERROR = 2;
    // 账户登录状态--失败其他错误
    public static Integer LOGIN_STATUS_OTHER = 3;
    // 是否已登录 1-登录
    public static Integer IS_LOGIN_UP = 1;
    // 是否已登录 2-未登录
    public static Integer IS_LOGIN_DOWN = 2;
    // redis前缀 验证码
    public static String CAPTCHA_CODE = "huiju:sys:captchaCode:";
    // redis前缀 是否展示验证码
    public static String SHOW_CAPTCHA = "huiju:sys:showCaptcha:";
    // redis前缀 密码错误次数
    public static String PASSWORD_ERROR = "huiju:sys:passworderror:";
    // redis前缀 sessionid
    public static String SESSION_ID = "huiju:sys:sessionid:";
    // redis前缀 验证码MsgID
    public static String MSG_ID = "huiju:sys:msgid:";

    // 品类字典Code
    public static String PROJECT_CLASSIFICATION = "classification";
    // 类别字典Code
    public static String PROJECT_CATEGORY = "project_category";
    // 店型字典Code
    public static String PROJECT_SHOPTYPE = "project_shopType";
    // 项目归属字典Code
    public static String PROJECT_ATTRIBUTIVE = "project_attributive";


}
