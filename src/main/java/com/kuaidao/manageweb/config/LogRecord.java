package com.kuaidao.manageweb.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.kuaidao.manageweb.constant.MenuEnum;

import java.lang.annotation.RetentionPolicy;

/**
 * 
 * @Description:     日志记录注解类， 使用在方法上  
 * @author: Chen Chengxue
 * @date:   2018年8月10日 上午9:34:50   
 * @version V1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogRecord {
  /**
   * 更新；添加；删除；导出；推送；
   */
  public enum OperationType{LOGIN,LOGINOUT,UPDATE,INSERT,DELETE,EXPORT,PUSH}
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

