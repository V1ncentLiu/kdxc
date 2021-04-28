package com.kuaidao.manageweb.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 1：先进先出 队列
 * 2：先进后出 栈
 * 3：循环链表
 * 添加上等待时间，就会形成阻塞队列
 */
@Component
public class RedisListUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取指定位置元素
     */
    public Object index(BoundListOperations operations,Long index){
        return operations.index(index);
    }

    /**
     *  获取队列大小
     */
    public Long size9(BoundListOperations operations){
        return operations.size();
    }

    /**
     * 使用两个列表构建循环列表
     *  右侧弹出，左侧插入。
     *  并返回弹出元素（针对同一key进行操作就是循环）
     */
    public Object getAll(String key1,String key2){
        return redisTemplate.opsForList().rightPopAndLeftPush(key1,key2);
    }

    /**
     * 获取全部数据
     */
    public List getAll(BoundListOperations operations){
        return operations.range(0,-1);
    }

    /**
     * 弹出元素
     */
    public Object pop(BoundListOperations operations, Boolean isLeft, Long dateLong,TimeUnit timeUnit){
        if(isLeft){
            return leftPop(operations,dateLong,timeUnit);
        }else{
            return rightPop(operations,dateLong,timeUnit);
        }
    }

    private Object leftPop(BoundListOperations operations, Long dateLong,TimeUnit timeUnit){
        if(dateLong==null){
            return operations.leftPop();
        }else{
            return operations.leftPop(dateLong,timeUnit);
        }
    }
    private Object rightPop(BoundListOperations operations, Long dateLong,TimeUnit timeUnit){
        if(dateLong==null){
            return operations.rightPop();
        }else{
            return operations.rightPop(dateLong,timeUnit);
        }
    }


    /**
     * 添加元素
     */
    public void  push(BoundListOperations operations, Collection list,Boolean isLeft){
        if(isLeft){
            for(Object o:list){
                operations.leftPush(o);
            }
        }else{
            for(Object o:list){
                operations.rightPush(o);
            }
        }

    }

    /**
     * 绑定key
     */
    public BoundListOperations boundKey(String key){
        return redisTemplate.boundListOps(key);
    }

}
