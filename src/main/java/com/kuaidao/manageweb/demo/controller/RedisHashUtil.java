package com.kuaidao.manageweb.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisHashUtil {

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 设置全部值
      */
    public void putAll(BoundHashOperations reidsObj, Map<String,Object> map){
        reidsObj.putAll(map);
    }

    /**
     *  设置单个值
     */
    public void put(BoundHashOperations reidsObj,String field,Object value){
        reidsObj.put(field,value);
    }

    /**
     * 获取到全部的值
     */
    public Map getAll(BoundHashOperations reidsObj){
        Map entries = reidsObj.entries();
        return entries;
    }
    /**
     * 获取多个字段值
     */
    public List multiGet(BoundHashOperations reidsObj,List keys){
         return  reidsObj.multiGet(keys);
    }
    /**
     * 获取全部的key
     */
    public Set keys(BoundHashOperations reidsObj){
        return reidsObj.keys();
    }
    /**
     * 获取全部的values
     */
    public List values(BoundHashOperations reidsObj){
        return reidsObj.values();
    }
    /**
     * 指定field进行自增或是自减
     */
    public void incOrDec(BoundHashOperations reidsObj,String field,Boolean isInc,Long i){
        if(isInc){
            reidsObj.increment(field,i);
        }else{
            reidsObj.increment(field,-1*i);
        }
    }
    /**
     * 删除字段
     */
    public void  delete(BoundHashOperations reidsObj,List fields){
        reidsObj.delete(fields);
    }
    /**
     * redis端做迭代
     */
    public void Scan(BoundHashOperations reidsObj){
        Cursor<Map.Entry<Object,Object>> cursor = reidsObj.scan(ScanOptions.scanOptions().match("*").count(1000).build());
        try{
            while (cursor.hasNext()) {
                Map.Entry<Object,Object> entry = cursor.next();
                // 这里进行具体的操作
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //cursor.close(); 游标一定要关闭，不然连接会一直增长
            //如果不关闭，连接会一直增长，直到卡死redis应用端连接池
            try {
                cursor.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 设置过期时间
     */
    public Boolean expire(BoundHashOperations reidsObj, Long num, TimeUnit timeUnit){
       return reidsObj.expire(num, timeUnit);
    }

    /**
     * 绑定key
     */
    public BoundHashOperations boundkey(String key){
        return  boundkey(key,null,null);
    }

    /**
     *  设置过期时间
     */
    public BoundHashOperations boundkey(String key,Long num, TimeUnit timeUnit){
        BoundHashOperations boundHashOperations = redisTemplate.boundHashOps(key);
        if(num!=null&&timeUnit!=null){
            expire(boundHashOperations,num,timeUnit);
        }
        return boundHashOperations;
    }
}
