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
    USER_MANAGEMENT("用户管理"), SCHEDULE_MANAGEMENT("任务管理"), ROLE_MANAGEMENT("角色管理"), AUTHORITY_MANAGEMENT("权限管理"), ACCOUNT_SUMMARY("账号汇总"), RESOURCE_MANAGEMENT("资源管理"), DICTIONARY_MANAGEMENT("字典管理"), DICTIONARY_MANAGEMENT_ITEM("字典-词条管理"), ANNOUNCE_MANAGEMENT("公告管理"), MESSAGE_CENTER("消息中心"), INVITEAREA("邀约区域管理"), TELEMARKTINGLAYOUT("电销布局管理"), ORGANIZATION_MANAGEMENT("组织机构管理"), CUSTOM_FIELD("自定义字段"), MODULE_MANAGEMENT("菜单管理"), IP_MANAGEMENT("IP管理"), IPPACKAGE_MANAGEMENT("IP包管理"),
    AREA_MANAGEMENT("区域管理"),


    /**
     * 电销管理 -业务设置
     */
    TR_CLIENT_MANAGEMENT("天润坐席管理"), QIMO_CLIENT_MANAGEMENT("七陌坐席管理"), COMPANY_MANAGEMENT("公司管理"), PROJECT_MANAGEMENT("项目管理"), ABNORMALUSER_MANAGENT("标记异常客户"), DEPTCALLSET_MANAGENT("部门呼叫设置"), ASSIGNRULE_INFO("信息流分配规则"), ASSIGNRULE_TELE("电销分配规则")


    /**
     * 电销管理 - 电销中心
     */
    ,TEL_CENTER_PUBLICCUSTOMER("公共客户资源"),TEL_CENTER_INVALIDCUSTOMER("无效客户资源")
    ;




    /**
     * 系统管理菜单
     */

    private String name;

    private MenuEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
