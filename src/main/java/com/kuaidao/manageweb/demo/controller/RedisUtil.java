package com.kuaidao.manageweb.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * redis 工具集
 *   redisson 框架
 */
@Component
public class RedisUtil {
    @Autowired
    RedisHashUtil redisHashUtil;
    @Autowired
    RedisListUtil redisListUtil;
    @Autowired
    RedisSortSetUtil redisSortSetUtil;
    @Autowired
    RedisValueUtil redisValueUtil;

    /**
     * 实现两种缓存两种模式：
     *  1：cache-aside
     *      常见操作
     *  2：cache as sor
     *      高级操作：
     */


    /**
     * 分布式锁
     */

    /**
     * 消息队列
     */
    /**
     *  实现秒杀功能
     */

    /**
     * 实现单点登录
     */

    /**
     * 实现session共享
     */
}
