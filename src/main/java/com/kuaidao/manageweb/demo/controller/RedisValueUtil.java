package com.kuaidao.manageweb.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RedisValueUtil {

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 针对字符串类型进行操作
     */
    public  Object getObj(String key){
       return redisTemplate.opsForValue().get(key);
    }

    /**
     * @param key
     * @param value
     * @param timeout 超时时间
     *       如果超时时间==null则不设置超时时间
     */
    public void setValue(String key,Object value, Duration timeout){
        if(timeout==null){
            redisTemplate.opsForValue().set(key,value);
        }else{
            redisTemplate.opsForValue().set(key,value,timeout);
        }
    }
    /**
     * 获取key的基本信息
     */
    public Map basicInfo(String key){
        BoundValueOperations boundValueOperations = redisTemplate.boundValueOps(key);
        Long expire = boundValueOperations.getExpire();
        DataType type = boundValueOperations.getType();
        HashMap<String, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("expire",expire);
        objectObjectHashMap.put("type",type);
        return objectObjectHashMap;
    }
    /**
     * 自增以及自减
     */
    public void incOrDec(String key,Long i,Boolean isInc){
        BoundValueOperations boundValueOperations = redisTemplate.boundValueOps(key);
        if(isInc){
            if(i==null){
                boundValueOperations.increment();
            }else{
                boundValueOperations.increment(i);
            }
        }else{
            if(i==null){
                boundValueOperations.decrement();
            }else{
                boundValueOperations.decrement(i);
            }
        }
    }

    /**
     * 批量获取
     */
    public List multi(List args){
        List list = redisTemplate.opsForValue().multiGet(args);
        return list;
    }
}
