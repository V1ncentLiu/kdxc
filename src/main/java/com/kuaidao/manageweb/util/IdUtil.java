package com.kuaidao.manageweb.util;

import com.kuaidao.common.util.Snowflake;
import com.kuaidao.manageweb.config.SnowflakeArgs;
import org.apache.logging.log4j.core.config.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Random;

/**
 * 主键ID 生成，基于雪花算法
 * 
 * @author: Chen Chengxue
 * @date: 2019年1月2日 上午9:20:19
 * @version V1.0
 */
@Component
@Order(2)
public final class IdUtil {

    private static Logger logger = LoggerFactory.getLogger(IdUtil.class);
    /**
     * 此类的实例，在应用内只允许有一个
     */
    private static Snowflake snowflake;


    /**
     * 雪花算法 参数
     */
    @Autowired
    SnowflakeArgs snowflakeArgs;


    private static Long workerId;

    /**
     * @param datacenterId 数据中心ID
     */
    private static Long datacenterId;


    private IdUtil() {

    }

    /**
     * 获取主键ID
     * 
     * @return
     */
    public static long getUUID() {
        if (snowflake == null) {
            synchronized (IdUtil.class) {
                if (workerId == null || datacenterId == null) {
                    logger.error("init snowflake error,workerId {{}},datacenterId {{}}", workerId,
                            datacenterId);
                    throw new IllegalArgumentException(
                            "execute IdUtil PostConstruct method error ,workerId or datacenterId is null");
                }
                logger.info("init snowflake ,workerId{{}},datacenterId{{}}", workerId,
                        datacenterId);
                snowflake = new Snowflake(workerId, datacenterId);
            }
        }

        return snowflake.nextId();
    }


    @PostConstruct
    private void getSnowFlakeParam() {
        if (snowflakeArgs == null) {
            throw new IllegalArgumentException(
                    "execute IdUtil PostConstruct method error ,snowflakeArgs is null");
        }
        String hostname = snowflakeArgs.getHostname();
        String workerIdStr = hostname.substring(hostname.lastIndexOf("-")+1);
        try {
            workerId = Long.parseLong(workerIdStr);
        } catch (NumberFormatException e) {
            logger.error("转换错误:hostname:{},workId：{}",hostname,workerIdStr);
        }
        if (null == workerId || workerId>31) {
            workerId = Long.valueOf(new Random().nextInt(31));
        }
        datacenterId = snowflakeArgs.getDatacenterId();
        logger.info("execute IdUtil PostConstruct method,hostname{{}},workerId{{}},datacenterId{{}}", hostname,workerId,
                datacenterId);
        if (datacenterId == null) {
            logger.error("init snowflake error,workerId {{}},datacenterId {{}}", workerId,
                    datacenterId);
            throw new IllegalArgumentException(
                    "execute IdUtil PostConstruct method error ,workerId or datacenterId is null");
        }
    }


}
