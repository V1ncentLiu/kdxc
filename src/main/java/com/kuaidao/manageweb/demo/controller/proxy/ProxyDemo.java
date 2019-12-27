package com.kuaidao.manageweb.demo.controller.proxy;

import lombok.extern.slf4j.Slf4j;

/**
 * @Auther: admin
 * @Date: 2019/11/19 18:02
 * @Description:
 */
@Slf4j
public class ProxyDemo {


  public String demo(){
    log.info("这就是一个demo");
    return "这就是一个demo";
  }

}
