package com.kuaidao.manageweb.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class RedisSortSetUtil {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加数据
     */
    public void add(String key ,Object value, double score){
        redisTemplate.opsForZSet().add(key,value,score);
    }

    /**
     * 添加全部
     */
    public void addAll(String key , Set<ZSetOperations.TypedTuple> set){
        redisTemplate.opsForZSet().add(key,set);
    }

    /**
     * 移除
     */
    public void remove(String key,Object object){
        redisTemplate.opsForZSet().remove(key,object);
    }

    /**
     *  移除排名范围内的成员
     */
    public void removeByRange(String key , Long start,Long end){
        redisTemplate.opsForZSet().removeRange(key,start,end);
    }

    /**
     *   移除分数范围的数据
     */
    public void removeWithRank(String key,Long start,Long end){
        redisTemplate.opsForZSet().removeRangeByScore(key,start,end);
    }

    /**
     * 获取集合中的成员数量
     */
    public Long count(String key){
        return redisTemplate.opsForZSet().size(key);
    }

    /**
     * 为成员加分
     */
    public void inc_score(String key,Object value,Double score){
        redisTemplate.opsForZSet().incrementScore(key,value,score);
    }
    /**
     * 获取交集并存储到另外一个集合中
     */
    public void intersection(String key1,String key2,String key3){
        redisTemplate.opsForZSet().intersectAndStore(key1,key2,key3);
    }

    /**
     * 返回指定成员变量的索引
     */
    public Long index(String key,Object object){
        return redisTemplate.opsForZSet().rank(key,object);
    }

    /**
     * 返回集合中的倒叙排名
     */
    public Long reverange(String key,Object object){
        return redisTemplate.opsForZSet().reverseRank(key,object);
    }
    /**
     * 返回指定分数区间的成员
     */
    public Set reverangeByScoreRange(String key,Long start,Long end){
       return redisTemplate.opsForZSet().reverseRangeByScore(key,start,end);
    }


    /**
     * 返回成员变量的分数
     */
    public Double score(String key,Object value){
        return redisTemplate.opsForZSet().score(key,value);
    }

    /**
     * 扫描
     */
    public void scan(String key) throws IOException {
        ScanOptions build = ScanOptions.scanOptions().match("*").count(1000).build();
        Cursor scan = redisTemplate.opsForZSet().scan(key, build);
        try{
            while(scan.hasNext()){
                Object next = scan.next();
            }
        }finally {
            scan.close();
        }

    }

}



