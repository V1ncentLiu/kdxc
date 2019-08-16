package com.kuaidao.manageweb.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.kuaidao.manageweb.constant.MenuEnum;

/**
 * 
 * @Description: 日志记录注解类， 使用在方法上
 * @author: Chen Chengxue
 * @date: 2018年8月10日 上午9:34:50
 * @version V1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogRecord {
    /**
     * 更新；LOGIN:登录； LOGINOUT:退出登录； INSERT:添加 UPDATE:编辑 DELETE:删除； EXPORT:导出；PUSH：推送；DISTRIBUTION：分配；
     * RELEASE:释放； TRANSFER:转移 DISABLE:禁用 ENABLE:启用 IMPORTS：导入 SCHEDULE：定时任务 RECEIVE：领取 OUTBOUNDCALL:外呼
     * CLIENT_LOGIN:坐席登录；CLIENT_LOGOUT：坐席退出
     */
    public enum OperationType {
        LOGIN, LOGINOUT, UPDATE, INSERT, DELETE, EXPORT, PUSH, IMPORTS, DISTRIBUTION, TRANSFER, RELEASE, DISABLE, ENABLE, SCHEDULE, RECEIVE, OUTBOUNDCALL
        ,CLIENT_LOGIN,CLIENT_LOGOUT
    }

    /**
     * 对该请求的描述
     */
    String description();

    /**
     * 该请求的操作类型，delete update insert
     */
    OperationType operationType();
    

    /**
     * 菜单名称
     */
    MenuEnum menuName();

}

