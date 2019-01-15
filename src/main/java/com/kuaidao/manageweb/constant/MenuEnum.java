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
     * 标签管理
     */
    TAG_RELEASE("标签发布"), TAG_CATEGORY("标签类别"),

    /**
     * banner图管理 菜单
     */
    BANNER_GALLERY("banner图库"),
    /**
     * 广告推荐 菜单
     */
    AD_RECOMMEND("广告推荐"),
    /**
     * 直播活动
     *
     */
    LIVE_CHANNEL("直播频道"), ACTIVITY_MGT("活动管理"), COUPON_MGT("优惠券管理"), CHATROOM_MONITORING("聊天室监控"),
    /**
     * 视频管理
     */
    VIDEO_RELEASE("视频发布"), VIDEO_RELEASE_MGT("视频发布管理"), VIDEO_AUDIT_MGT("视频审核管理"), VIDEO_MGT("视频整理"),
    /**
     * 评论管理
     */
    COMMENT_SUMMARY("评论汇总"), COMMENT_AUDIT("评论审核"), COMMENT_RES("评论回复"), BATCH_COMMENT("批量评论"), ACTIVITY_COMMENT_AUDIT("活动评论审核"),
    /**
     * 经纪人账号
     */
    IDENTITY_MGT("身份管理"), AGENT_ACCOUNT("经纪人账号"), AGENT_ACCOUNT_SUMMARY("账号汇总"), PUB_AGENT_MGT("宣传经纪人管理"),
    /**
     * 客服中心
     */
    ONLINE_COMM_RECORD("在线沟通记录"), MY_CUSTOMER_TEL("我的客户(客服)"), MY_CUSTOMER_IM("我的客户(即时通讯)"),
    /**
     * 业务设置
     */
    CLIENT_MGT("坐席管理"), QUEUE_MGT("队列管理"), RELEATED_BRAND("关联品牌"), ONLINE_SERVICE_SETTING("在线客服设置"),
    /**
     * 客服中心账号
     */
    CUST_SERVICE_IDENTITY_MGT("身份管理"), CUST_SERVICE_ACCOUNT("客服账号"), CUST_SERVICE_ACCONT_SUMMARY("账号汇总"), RESGISTRATION_CLIENT_SUMMARY("注册客户统计"), ACTIVITY_USER_INFO("活动用户信息"), MISSING_CALL("未接客服来电"), USER_MESSAGE("用户留言"), CHANNEL_MSG_MGT("渠道留言管理"), USER_FEEDBACK("用户反馈"), COMMENT_CLIENT_INFO("评论客户信息"),
    /**
     * 电销中心账号
     */
    TM_AGENT_MGT("坐席管理"), TM_IDENTITY_MGT("身份管理"), TM_ACCOUNT("电销账号"), TM_ACCOUNT_SUMMARY("电销账号汇总"),
    /**
     * 话务中心账号
     */
    TRAFFIC_AGENT_MGT("坐席管理"), TRAFFIC_IDENTITY_MGT("身份管理"), TRAFFIC_ACCOUNT("电销账号"), TRAFFIC_ACCOUNT_SUMMARY("电销账号汇总"),
    /**
     * 话务中心
     * 
     */
    TRAFFIC_MY_CUSTOMER("我的客户"), TRAFFIC_IMPORT_RESOURCE_MGT("导入资源管理"),

    /**
     * 资讯管理
     */
    INFORMATION_RELEASE("资讯发布"),
    /**
     * 版本管理
     */

    IOS_VERSION_INFO_INPUT("IOS版本信息录入"), ANDROID_VERSION_INFO_INPUT("安卓版本信息录入"), IOS_VERSION_LIST("IOS版本列表"), ANDROID_VERSION_LIST("ANDROID版本列表"),

    /**
     * 活动页管理
     * 
     */
    CREATE_ACTIVITY_PAGE("新增活动页"),

    /**
     * 电销中心
     */

    TM_CLIENT_MGT_2("客户管理"), TM_CALL_RECORDS("电销通话记录"), TM_CALL_TIME_SUMMARY("电销通话时长统计"), TM_CUSTOMER_ALLOCATE("客户分配(电销)"), TM_MY_CUSTOMER("我的客户(电销)"),
    /**
     * 消息推送管理
     */
    CREATE_MSG_PUSH("新增消息推送"), MSG_PUSH_RECORDS("消息推送记录"),
    /**
     * 消息通知管理
     */
    CREATE_MSG_NOTIFY("新增消息通知"), MSG_NOTIFY_RECORDS("消息通知记录"),
    /**
     * 城市管理
     */
    HOT_CITY_MGT("热门城市管理"),
    /**
     * 渠道管理
     */
    CHANNEL_SETTING_MGT("渠道设置管理"),
    /**
     * VR视频管理
     */
    VR_VIDEO_GALLERY("VR视频库"),

    /**
     * 品牌账号
     */
    BRAND_ACCOUNT("品牌账号"), BRAND_ACCOUNT_SUMMARY("账号汇总"),
    /**
     * 品牌管理
     */
    BRAND_APPLY("入住申请"), BRAND_INFORMATION("品牌信息"), BRAND_AUDIT("品牌审核"), BRAND_RELEASE("品牌发布"), BRAND_SUMMARY("品牌汇总"),

    /**
     * 加盟顾问中心
     */
    JOINCOUNSELOR_CUSTOMER_MGR("客户管理"), JOINCOUNSELOR_MY_CUSTOMER("我的客户");

    private String name;

    private MenuEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
