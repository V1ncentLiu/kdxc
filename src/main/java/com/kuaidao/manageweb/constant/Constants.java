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
    // 是否是优化类资源1是
    public static Integer IS_OPTIMIZE = 1;
    // 是否是优化类资源2否
    public static Integer IS_NOT_OPTIMIZE = 2;
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

    // 七陌坐席 redis key
    public static String QIMO_CLIENT_KEY = "huiju:aggregation:client:qimo";
    // 乐创坐席 redis key
    public static String LC_CLIENT_KEY = "huiju:aggregation:client:lc";
    // 中科坐席 redis key
    public static String ZK_CLIENT_KEY = "huiju:aggregation:client:zk";
    // 天润坐席 reids key
    public static String TR_CLIENT_KEY = "huiju:aggregation:client:tr";
    // 合力坐席 reids key
    public static String HELI_CLIENT_KEY = "huiju:aggregation:client:heli";

    // 品类字典Code
    public static String PROJECT_CLASSIFICATION = "industryCategory";
    // 类别字典Code
    public static String PROJECT_CATEGORY = "project_category";
    // 店型字典Code
    public static String PROJECT_SHOPTYPE = "vistitStoreType"; // project_shopType
    // 项目归属字典Code
    public static String PROJECT_ATTRIBUTIVE = "project_attributive";
    // 广告位字典Code
    public static String ADSENSE = "adsense";
    // 媒介字典Code
    public static String MEDIUM = "medium";
    // 行业类别字典Code
    public static String INDUSTRY_CATEGORY = "industryCategory";
    // 资源类别字典Code
    public static String CLUE_CATEGORY = "clueCategory";
    // 资源类型字典Code
    public static String CLUE_TYPE = "clueType";
    // 选址情况字典Code
    public static String OPTION_ADDRESS = "optionAddress";
    // 合伙人字典Code
    public static String PARTNER = "partner";
    // 餐饮经验字典Code
    public static String CATERING_EXPERIENCE = "CateringExperience";
    // 到访店型字典Code
    public static String VISTIT_STORE_TYPE = "vistitStoreType";
    // 店铺面积字典Code
    public static String STOREFRONT_AREA = "StorefrontArea";
    // 投资金额字典Code
    public static String USSM = "investmentAmount";
    // 释放原因字典Code
    public static String RELEASE_REASON = "releaseReason";

    public static String SETTLEMENT_RATIO = "settlement_ratio";
    // 释放原因字典Code
    public static String GIVE_TYPE = "give_type";
    // 非优化类资源导入模板列数
    public static Integer UNOPTIMIZE = 23;
    // 优化类资源导入模板列数
    public static Integer OPTIMIZE = 21;
    /**
     * 不限
     **/
    public static String NO_LIMIT = "-1";
    /**
     * 不带走资源
     ***/
    public static Integer NOT_TAKE_AWAY_CLUE = 0;
    /**
     * 带走资源
     ***/
    public static Integer TAKE_AWAY_CLUE = 1;
    /** redis前缀 看板计数-电销顾问计数-今日 **/
    public static final String TODAY_TELE_SALE = "huiju:dashboard:todaytelesale:";
    /** redis前缀 看板计数-电销顾问计数-今日 **/
    public static final String NOTTODAY_TELE_SALE = "huiju:dashboard:nottodaytelesale:";
    /** 餐盟首次登录 **/
    public static final String  CM_DXGW_FIRST_LOGIN = "huiju:sys:cmfirstlogin:";
    /**
     * 返回码成功
     */
    public static final String SUCCESS = "0";
    /**
     * 收款户名
     */
    //todo
    public static final String PAYMENT_NAME = "???";
    /**
     * 收款银行
     */
    //todo
    public static final String BANK = "???";
    /**
     * 收款账号
     */
    //todo
    public static final String PAYMENT_ACCOUNT = "???";
    /**
     * 商家主账号
     */
    public static Integer MERCHANT_PRIMARY_ACCOUNT = 2;
    /**
     * 七陌呼叫类型：手机外显
     */
    public static final String BIND_TYPE_TWO = "2";
    /**
     * 七陌呼叫类型：普通外呼
     */
    public static final String BIND_TYPE_ONE = "1";


    //慧聚
    public  static  Integer USER_TYPE_ONE = 1;
    //商家主账号
    public  static  Integer USER_TYPE_TWO = 2;
    //商家子账号
    public  static  Integer USER_TYPE_THREE = 3;

    //支付宝
    public  static  Integer RECHARGE_WAY_ALI_PAY = 1;
    //微信
    public  static  Integer RECHARGE_WAY_WEXIN_PAY = 2;

    // redis前缀 验证码MsgID
    public static String REDIS_NEXT_PREFIX = "huiju:user:next:";
}
