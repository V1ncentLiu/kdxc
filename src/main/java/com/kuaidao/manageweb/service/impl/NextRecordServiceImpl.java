package com.kuaidao.manageweb.service.impl;

import com.alibaba.excel.util.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.kuaidao.aggregation.dto.clue.CustomerClueDTO;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.service.NextRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author admin
 * 我的客户下一条记录业务
 */
@Slf4j
@Service
public class NextRecordServiceImpl implements NextRecordService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 我的客户记录压入缓存
     * @param userId
     * @param customerClueDTOList
     */
    @Override
    public void pushList(Long userId , List<CustomerClueDTO> customerClueDTOList){

        if(CollectionUtils.isEmpty(customerClueDTOList)){

            log.warn("push-list,参数为空!");
            return;
        }

        LinkedList<Long> linkedList = customerClueDTOList.stream().map(CustomerClueDTO::getClueId).collect(Collectors.toCollection(LinkedList::new));
        // todo 四小时超时 ，key 类型
        redisTemplate.opsForValue().set(Constants.REDIS_NEXT_PREFIX + userId , JSON.toJSONString(linkedList) );

        redisTemplate.expire(Constants.REDIS_NEXT_PREFIX + userId  , 4, TimeUnit.HOURS);
    }


    /**
     * 获得下一条记录
     * @param userId
     * @param currentClueId 当前记录
     * @return
     */
    @Override
    public Long next( Long userId , Long currentClueId){

        String redisCacheValue = redisTemplate.opsForValue().get(Constants.REDIS_NEXT_PREFIX + userId);
        @SuppressWarnings("unchecked")
        LinkedList<Long> targetList = JSON.parseObject(redisCacheValue, LinkedList.class);

        if(CollectionUtils.isEmpty(targetList)){
            // 不存在,提示到底
            return -1L ;
        }
        for(Iterator<Long> iterator = targetList.iterator(); iterator.hasNext();){

            Long next = iterator.next();

            if(currentClueId.equals(next) ){
                // 移除
                iterator.remove();
                break;
            }
            // 移除该元素
            iterator.remove();
        }
        if(CollectionUtils.isEmpty(targetList)){
            // 不存在,提示到底
            return -1L ;
        }
        // 再存存储
        redisTemplate.opsForValue().set(Constants.REDIS_NEXT_PREFIX + userId , JSON.toJSONString(targetList));

        redisTemplate.expire(Constants.REDIS_NEXT_PREFIX + userId  , 4, TimeUnit.HOURS);

        Long first = targetList.getFirst();
        // 返回第一个
        return null == first ? -1L : first;
    }
}
