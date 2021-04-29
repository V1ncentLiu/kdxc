package com.kuaidao.manageweb.demo.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * redis 中应用的各种方式
 *   两种大模式：
 *      1：cache-aside
 *      2：cache as Ros
 */

@Slf4j
@Controller
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedisTemplate redisTemplate;


    @ResponseBody
    @RequestMapping("/get")
    public void  get() {
//        redisTemplate.watch("key");

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
                HashOperations<K, Object, Object> kObjectObjectHashOperations = redisOperations.opsForHash();
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


    /**
     * redis 字符串相关操作
     */
    private void stringOperations(){
        final String key = "yangbiao";
        ValueOperations valueOperations = redisTemplate.opsForValue();
        BoundValueOperations boundValueOperations = valueOperations.getOperations().boundValueOps(key);
        Object o = boundValueOperations.get();
        if(o==null){
            boundValueOperations.set("this is demo");
            o = boundValueOperations.get();
        }
        // 这个是啥意思啊
//        boundValueOperations.append("  appppppp");
//        o = boundValueOperations.get();
        // redis 中的自增操作
        valueOperations.set("inc",1);
        valueOperations.increment("inc");
        log.info("inc value::{}",valueOperations.get("inc"));
        valueOperations.increment("inc",100L);
        log.info("inc value::{}",valueOperations.get("inc"));
        valueOperations.decrement("inc",10);
        log.info("inc value::{}",valueOperations.get("inc"));

        // 获取多个值
        List list = valueOperations.multiGet(Arrays.asList("inc", key));
        log.info("list value::{}",list);
        Long size = valueOperations.size(key);
        log.info("size value::{}",size);
        // 存储对象.在key-value中存储对象其实就是一个序列化和反序列化的过程
        Datas datas = new Datas();
        datas.setAge(11);
        datas.setDate(new Date());
        datas.setId(111L);
        datas.setName("yangbiao");
        valueOperations.set("obj",datas);
        log.info("obj value::{}",valueOperations.get("obj"));

        boundValueOperations.set("设置超时时间", Duration.ofMinutes(1000));
        DataType type = boundValueOperations.getType();
        Object key1 = boundValueOperations.getKey();
        // 这里面是按照秒进行的输出
        Long expire = boundValueOperations.getExpire();
        log.info("key::{}",key1);
        log.info("value::{}",o);
        log.info("DataType,{}",type);
        log.info("expire,{}",expire);
    }

    /**
     redis中hash操作
     **/
    private void hashOperations(){
        final String key = "yangHash";
        BoundHashOperations boundHashOperations = redisTemplate.boundHashOps(key);
        Object o = boundHashOperations.get(key);
        Datas datas = new Datas();
        datas.setAge(23);
        datas.setId(11L);
        datas.setName("yangbiao");
        if(o==null){
            boundHashOperations.put("id",datas);
            o = boundHashOperations.get("id");
        }
        log.info("value:{}",o);
        HashMap<String, Object> stringMap = new HashMap<>();
        stringMap.put("key1","111");
        stringMap.put("key2",111);
        stringMap.put("key3",datas);
        stringMap.put("key4",new ArrayList<>());
        boundHashOperations.putAll(stringMap);
        Map entries = boundHashOperations.entries();
        log.info("value:{}",entries);
        boundHashOperations.delete("key1");
        log.info("value:{}",boundHashOperations.entries());
        log.info("value:{}", boundHashOperations.multiGet(Arrays.asList("key1","key2","key4")));
        log.info("keys:{}",boundHashOperations.keys());
        log.info("values:{}",boundHashOperations.values());
        // 在redis端遍历数据
        /**
         * keys的操作会导致数据库暂时被锁住，其他的请求都会被堵塞；业务量大的时候会出问题
         */
        Cursor<Map.Entry<Object,Object>> cursor = boundHashOperations.scan(ScanOptions.scanOptions().match("*").count(1).build());
        try{
            while (cursor.hasNext()) {
                Map.Entry<Object,Object> entry = cursor.next();
                log.info("scan:{},{}",entry.getKey(),entry.getValue());
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
}
