package com.kuaidao.manageweb.constant;

/**
 *
 * @Description: 维护后台的所有菜单，为了记录访问日志使用
 * @author: Chen Chengxue
 * @date: 2018年8月13日 上午9:02:21
 * @version V1.0
 */
public enum MenuEnum {
    INDEX("首页"), LOGIN("登录"), LOGINOUT("退出登录"), UPDATE_PASSWORD("修改密码"), GET_CODE("获取验证码"),
    /**
     * 系统管理菜单
     */
    USER_MANAGEMENT("用户管理"), SCHEDULE_MANAGEMENT("任务管理"), ROLE_MANAGEMENT("角色管理"), AUTHORITY_MANAGEMENT("权限管理"), ACCOUNT_SUMMARY("账号汇总"), RESOURCE_MANAGEMENT("资源管理"), DICTIONARY_MANAGEMENT("字典管理"), DICTIONARY_MANAGEMENT_ITEM("字典-词条管理"), ANNOUNCE_MANAGEMENT("公告管理"), MESSAGE_CENTER("消息中心"), INVITEAREA("商务布局管理"), TELEMARKTINGLAYOUT("电销布局管理"), ORGANIZATION_MANAGEMENT("组织机构管理"), CUSTOM_FIELD("自定义字段"), MODULE_MANAGEMENT("菜单管理"), IP_MANAGEMENT("IP管理"), IPPACKAGE_MANAGEMENT("IP包管理"), AREA_MANAGEMENT("区域管理"), MERCHANT_USER_MANAGEMENT("商家账号管理"), MERCHANT_ORGANIZATION_MANAGEMENT("商家组织管理"),


    /**
     * 电销管理 -业务设置
     */
    TR_CLIENT_MANAGEMENT("天润坐席管理"), KETIAN_CLIENT_MANAGEMENT("科天坐席管理"), HELI_CLIENT_MANAGEMENT("合力坐席管理"), QIMO_CLIENT_MANAGEMENT("七陌坐席管理"), CLUE_CHARGE_MANAGEMENT("资源资费管理"), COMPANY_MANAGEMENT("公司管理"), PROJECT_MANAGEMENT("项目管理"), ABNORMALUSER_MANAGENT("标记异常客户"), DEPTCALLSET_MANAGENT("部门呼叫设置"), ASSIGNRULE_INFO("信息流分配规则"), ASSIGNRULE_TELE("电销分配规则"), APPIONTMENT_MANAGEMENT("预约来访记录"), PENDING_ALLOCATION_CLUE("待分配资源"), BUS_ALLOCATION_CLUE("待分配来访记录"), BUS_CUSTOMER_MANAGER("商务客户管理"), FINANCELAYOUT("财务电销布局"), TM_MY_CUSTOMER("我的客户(电销)"), BUS_MY_CUSTOMER("我的客户(商务)"), TRUCKING_ORDER_PAGE("邀约来访派车单"), TELE_CUSTOMER_MANAGER("电销客户管理"), CUSTOMER_INFO("客户详情"),
    /**
     * 电销管理 - 电销中心
     */
    TEL_CENTER_PUBLICCUSTOMER("公共客户资源"), TEL_CENTER_INVALIDCUSTOMER("无效客户资源"), OPT_RULE_MANAGEMENT("优化分配规则管理"), MERCHANT_RULE_MANAGEMENT("商家规则管理"), NOT_OPT_RULE_MANAGEMENT("非优化分配规则管理"), TRAFFIC_RULE_MANAGEMENT("话务分配规则管理"),
    /**
     * 总裁办
     *
     */
    REPETITION("重单处理（总裁办）"), BUSINESSSIGNVALID("有效签约单确认"), BUSINESSSIGNREPETITION("付款明细重单审批确认（总裁办）"), PAYDETAILREPETITION("付款明细分配比例（总裁办）"),
    /**
     * 电销中心
     */
    BUSS_MANAGER("商务管理"), SIGN_ORDER("签约记录"), CUSTOMER_VISIT_RECORD("来访记录"), CLUE_RELEASE_RECEIVE_RULE("资源释放领取规则"),
    /**
     * 话务
     */
    PHONETRAFFIC_MANAGER("话务管理"),
    /**
     * 资源管理
     */
    WAIT_DISTRIBUT_RESOURCE("待分发资源"), CLUE_TEMPLATE_MANAGEMENT("资源模板管理"), CLUE_RULE_REPORT("规则报表"),
    /**
     * 财务
     */
    REFUNDREBATE_MANAGER("退返款申请"), REFUNDAPPLYLIST("退款申请列表"), REFUNDCONFIRM("退款确认"), REBATEAPPLYLIST("返款申请列表"), REBATECONFIRM("返款确认"), RECONCILIATIONCONFIRM_MANAGER("对账结算确认"), REFUNDREBATEAPPLY_MANAGER("对账结算申请"),
    /**
     * 版本管理
     */
    VERSION_LIST("版本列表"), IOS_VERSION_LIST("IOS版本列表"), ANDROID_VERSION_LIST("ANDROID版本列表"),
    /**
     * 商家端
     */
    AUDIT_PASS("资源需求申请审核通过"), AUDIT_REJECT("资源需求申请审核驳回"),CLUE_MANAGEMENT("资源管理列表"),
    CONSUME_RECORD("消费记录（管理端）"),MERCHANT_OUT_CALL("商家外呼"),

    OUTBOUND_PACKAGE("服务费用设置"),MERCHANT_CLIENT_LOGIN("坐席自动登录"),
    /**
     * 报表（商务模块）
     */
    VISIT_SIGN("签约来访");


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
