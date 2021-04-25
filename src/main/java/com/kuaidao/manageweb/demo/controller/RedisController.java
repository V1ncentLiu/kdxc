package com.kuaidao.manageweb.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedisTemplate redisTemplate;


    @RequestMapping("/get")
    public String  get() {

        return "success";
    }

    /**
     * redis 字符串相关操作
     */
    private void stringOperations(){
        final String prifix = "demo:";
        redisTemplate.opsForValue().set(prifix+"1","1");
        redisTemplate.opsForValue().get("");
        Long start = 1L;
        Long end = 10L;
        redisTemplate.opsForValue().get("",start,end);
        redisTemplate.opsForValue().multiGet(Arrays.asList("key1","key2"));
        // 同时设置多个值
        Map map = new HashMap();
        redisTemplate.opsForValue().multiSet(map);
        redisTemplate.opsForValue().setIfAbsent("",""); // 如果不存在时候进行设置
        // 自增计算,自增1，自增指定长整形，自增指定浮点数
        redisTemplate.opsForValue().increment("");
        // 自减操纵
        redisTemplate.opsForValue().decrement("");
        // 追加操纵
        redisTemplate.opsForValue().append("","wewe");
    }



    /**
     redis中hash操作
    **/
    private void hashOperations(){
        // 删除一个或是多个hash表字段
        hashObj().delete("",Arrays.asList("","",""));
        // 查询Hash表中的字段是否存在架
        hashObj().hasKey("key","field");
        // 获取hash中的指定字段
        hashObj().get("key","field");
        // 获取对象中的指定的多个字段
        hashObj().multiGet("key",Arrays.asList(""));
        // 指定字段增量计算
        hashObj().increment("key","field",1);
        // 获取hash中的所有key
        hashObj().keys("key");
        // 设置指定字段的value
        hashObj().put("key","field","value");
        // 获取key中的全部字段的值
        hashObj().values("key");
        // 设置全部的值
        hashObj().putAll("key",new HashMap());
        // 如果字段不存在怎新增一个字段
        hashObj().putIfAbsent("","","sss");
        // 这个东西是做什么的
        hashObj().size("key");
        // 这种运行在redis上的循环遍历是怎样的操作
        //DOTO java操作的这种东西还是还是需要更加详细的理解啊
        ScanOptions.ScanOptionsBuilder scanOptionsBuilder = ScanOptions.scanOptions();
        ScanOptions build = scanOptionsBuilder.build();
        hashObj().scan("key",build);

    }
    private HashOperations hashObj(){
        return  redisTemplate.opsForHash();
    }

    private void listOperations(){
        // 获取数组中的第一个值
        listObj().index("key",1);
        listObj().size("key");// 列表长度
        listObj().leftPop("key");// 移除并获取列表中的第一个元素
        // 移除并获取第一个元素，如果没有则等待对应超时时间
        listObj().leftPop("key",10,TimeUnit.MILLISECONDS);
        // 左侧添加元素
        listObj().leftPush("","");
        // 同时将两个值插入
        listObj().leftPush("key","2","3");
        // 插入多个值
        listObj().leftPushAll("ke","2","2",2,3,4,5);
        // 同时插入多个值
        listObj().leftPushAll("kye",Arrays.asList(""));
        // 这个东西是干啥用的
        Long aLong = listObj().leftPushIfPresent("", 1);
        // 返回指定方位元素
        listObj().range("key",1,10);
//        listObj().trim(); // 列表截取
        // 修改指定序号下的值
        listObj().set("key",1,"wewe");
        // 右侧弹出左侧添加.将一个元素转移到另一个列表中
        listObj().rightPopAndLeftPush("","");
        // 移除列表元素
        listObj().remove("key",1,"333"); // zhege
      }




    /**
     * redis 中的列表操作
     */
    private ListOperations listObj(){
        return redisTemplate.opsForList();
    }

    private void setOperations(){
        setObj().add("","");
        setObj().difference("key1","key2");
        setObj().difference("key",Arrays.asList(""));
        // 比较差集，并存储在另一个集合中
        setObj().differenceAndStore("k1","k2","k3");
        // 返回集合中2个随机值
        setObj().distinctRandomMembers("k1",2);
        //  交集
        setObj().intersect("k1","k2");
        setObj().intersect("k1",Arrays.asList(""));
        // 交集并存储
        setObj().intersectAndStore("k1","k2","k3");
        // 集合中是否存在
        setObj().isMember("k1",1);
        // 返回集合中的全部数据
        setObj().members("");
        // 连接两个集合
        Set union = setObj().union("k1", "k2");
        setObj().union("k1",Arrays.asList(""));
        // 连接两个集合并存储
        setObj().unionAndStore("k1","k2","k3");
        // 集合大小
        setObj().size("k1");
        // 从集合中移除
        setObj().remove("k1",1,2,3,4);
        // 创建扫描
        setObj().scan("k2",ScanOptions.scanOptions().build());
        // 从集合1中将元素移动到集合3中
        setObj().move("k1","2222","k3");
        // 移除并返回集合中的一个元素
        setObj().pop("k1",1);
        // 返回一个随机元素
        setObj().randomMember("k1");
    }


    /**
     * redis 中的集合操作
     */
    private SetOperations setObj(){
        return redisTemplate.opsForSet();
    }

    /**
     * redis 中的有序集合操作
     */
    private ZSetOperations zsetObj(){
        return redisTemplate.opsForZSet();
    }

    /**
     * 有序集合操作
     */
    private void zsetOpeartions(){
        BoundZSetOperations zkey = redisTemplate.boundZSetOps("k1");
        zkey.add("valu1",1d);
        //同时添加多个值，并设置分数
        DefaultTypedTuple<String> p1 = new DefaultTypedTuple<>("zSetVaule1", 2.1D);
        DefaultTypedTuple<String> p2 = new DefaultTypedTuple<>("zSetVaule2", 3.3D);
        zkey.add(new HashSet<>(Arrays.asList(p1,p2)));
        // 返回指定区间的元素 -1 返回全部元素.按照排名先后。从小到大
        Set range = zkey.range(0, -1);
        // 获取指定分数
        zkey.score("v1");
        // 获取集合中的数量
        zkey.size();
        // 返回指定分数范围内的元素个数
        zkey.count(10D,100D);
        // 返回分数范围内的元素
        zkey.rangeByScore(10D,100D);

    }


    /**
     * 基数计算统计
     */
    private void hyperLogLogOperationsDemo(){
        // 基数统计集合中添加key
        hyperLogLogOperations().add("k1",1,2,3,4,5,6);
        // 删除key
        hyperLogLogOperations().delete("k1");
        // 获取指定key的大小
        hyperLogLogOperations().size("k1");
        // 连接多个集合
        hyperLogLogOperations().union("k1","k2","k3");
    }

    /**
     *  基数统计
     */
    private HyperLogLogOperations hyperLogLogOperations(){
        HyperLogLogOperations hyperLogLogOperations = redisTemplate.opsForHyperLogLog();
        return  hyperLogLogOperations;
    }


    /**
     * redis 中的发布订阅操作
     */

    /**
     * redis 中的流操作
     */

    /**
     * redis 中的管道技术
     * 其实就是将多个命令发送redis然后返回多个结果。
     * 好处是，将多次网络请求的开销，编变成一次。
     * 使用pipLine执行多条命令
     */
    private void pipOperations(){
        // 总共提供了四种管道操作
        // 操作的是 RedisOperations
        List list = redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                return null;
            }
        });
        // 这里面添加了相关的序列化器
        redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                return null;
            }
        }, RedisSerializer.java());
        // 操作的是RedisConnection
        redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return null;
            }
        });

    }

    /**
     * redis key相关操作
     */
    private void keyOperations(){
        // 删除key
        redisTemplate.delete("");
        // 序列化key并返回相关的序列化值
        redisTemplate.dump("");
        // 判断key是否存在, 如果存在则返回存在的数量
        redisTemplate.countExistingKeys(Arrays.asList(""));
        // 设置过期时间 TimeUnit 指定了对应的时间单位
        redisTemplate.expire("",10000L, TimeUnit.MICROSECONDS);
        // 查询所有符合给定匹配模式的key
        redisTemplate.keys("yang*");
        // 将当前数据库中的key移动到目标库
        redisTemplate.move("",1);
        // 移除key的过期时间，可以将保持持久化
        redisTemplate.persist("");
        // 返回key的过期时间(java中好像没有对应的api)
        // 返回一个随机key
        redisTemplate.randomKey();
        // 修改key名称
        redisTemplate.rename("old","new");
        // 仅当 newkey 不存在时，将 key 改名为 newkey 。
        redisTemplate.renameIfAbsent("old","new");
        // 返回key的数据类型
        redisTemplate.type("");
        // 迭代数据库中的key值 java中也没有为这个api设定相关操作

    }



}
