package com.kuaidao.manageweb.constant;

/**
 * <br>
 * 类 名:ErrorCodeEnum <br>
 * 描 述:评论管理服务错误码枚举类 作 者:zhangxingyu<br>
 * 创 建:2017年4月20日 <br>
 * 版 本:v0.0.1 <br>
 * <br>
 * 历 史:(版本) 作者 时间 注释
 */
public enum ManagerWebErrorCodeEnum {

    NONE("-1", "未知错误"),

    ERR_CODE_ERROR("21601", "验证码填写错误!"),

    ERR_CODE_TIMEOUT("21602", "验证码已过期！"),

    ERR_SESSION_TIMEOUT("21603", "Session失效！"),

    ERR_CODE_NULL("21604", "请输入验证码！"),

    ERR_LOGIN_ERROR("21606", "登录错误"),

    ERR_RESET_LOGIN("21605", "请重新登录！");

    private String code;
    private String message;

    private ManagerWebErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static ManagerWebErrorCodeEnum resolve(Integer code) {

        switch (code) {
            case 21601:
                return ERR_CODE_ERROR;
            case 21602:
                return ERR_CODE_TIMEOUT;
            case 21603:
                return ERR_SESSION_TIMEOUT;
            case 21604:
                return ERR_CODE_NULL;
            case 21605:
                return ERR_RESET_LOGIN;
            case 21606:
                return ERR_LOGIN_ERROR;
            default:
                return NONE;
        }
    }
}
