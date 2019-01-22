package com.kuaidao.manageweb.constant;

/**
 * 
 * @Description: 维护后台的所有菜单，为了记录访问日志使用
 * @author: Chen Chengxue
 * @date: 2018年8月13日 上午9:02:21
 * @version V1.0
 */
public enum MenuEnum {
    LOGIN("登录"), LOGINOUT("退出登录"), UPDATE_PASSWORD("修改密码"), GET_CODE("获取验证码"),
    /**
     * 系统管理菜单
     */
    USER_MANAGEMENT("用户管理"), SCHEDULE_MANAGEMENT("任务管理"), ROLE_MANAGEMENT("角色管理"), AUTHORITY_MANAGEMENT("权限管理"), ACCOUNT_SUMMARY("账号汇总"), RESOURCE_MANAGEMENT("资源管理"), DICTIONARY_MANAGEMENT("字典管理"), ANNOUNCE_MANAGEMENT("公告管理"), MESSAGE_CENTER("消息中心"),

    
    /**
     * 电销管理 -业务设置
     */
    TR_CLIENT_MANAGEMENT("天润坐席管理"),  QIMO_CLIENT_MANAGEMENT("七陌坐席管理");
    
    
    private String name;

    private MenuEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
