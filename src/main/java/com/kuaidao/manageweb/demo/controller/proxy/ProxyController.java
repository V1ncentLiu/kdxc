//package com.kuaidao.manageweb.demo.controller.proxy;
//
//
//import static cn.hutool.aop.ProxyUtil.*;
//
//import cn.hutool.aop.ProxyUtil;
//import cn.hutool.aop.aspects.Aspect;
//import java.lang.reflect.Method;
//
///**
// * @Auther: admin
// * @Date: 2019/11/19 17:59
// * @Description:
// */
//public class ProxyController {
//
//  public void proxyDemo(){
//    // 生成动态代理
//    ProxyUtil.proxy(new ProxyDemo(),new Aspect(){
//      @Override
//      public boolean before(Object o, Method method, Object[] objects) {
//        return false;
//      }
//
//      @Override
//      public boolean after(Object o, Method method, Object[] objects) {
//        return false;
//      }
//
//      @Override
//      public boolean afterException(Object o, Method method, Object[] objects,
//          Throwable throwable) {
//        return false;
//      }
//    });
//
//    // 以上两种都是生成动态代理的方法
//    ProxyUtil.proxy(new ProxyDemo(),AspectDemo.class);
//  }
//
//}
