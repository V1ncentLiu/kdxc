package com.kuaidao.manageweb.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 当前版本并没有相关stream的操作
 */
@Component
public class RedisStreamUtil {
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 消息接收
     */
    public void ddd(){
    }

    /**
     * 发布订阅
     */


}
