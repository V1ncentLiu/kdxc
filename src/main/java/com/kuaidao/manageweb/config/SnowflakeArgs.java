package com.kuaidao.manageweb.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 雪花算法参数，项目启动时传递参数  
 * 如：  --workerId=2 --datacenterId=3
 * @author: Chen Chengxue
 * @date: 2019年1月2日 上午9:36:00   
 * @version V1.0
 */
@Component
@Data
@Order(1)
public class SnowflakeArgs {
    
    /**
     * @param hostname 容器名
     */
    @Value("${HOSTNAME}")
    private String hostname;
    
    /**
     * @param datacenterId 数据中心ID
     */
    @Value("${datacenterId}")
    private Long datacenterId;

}
