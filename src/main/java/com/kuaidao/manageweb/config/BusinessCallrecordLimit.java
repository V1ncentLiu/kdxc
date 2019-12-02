package com.kuaidao.manageweb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;
import org.springframework.stereotype.Component;


/**
 * 商学院通话录音 约束
 * @author  Devin.Chen
 * @date 2019-07-11 17:24:03
 * @version V1.0
 */
@Component
@Configuration
@Data
@ConfigurationProperties(prefix = "callrecord.business-org-id")
public class BusinessCallrecordLimit {
    /**
     * qhdBusOrgId
     */
    private Long qhdBusOrgId;
    /**
     * 郑州商学院
     */
    private String zzBusOrgId;
    /**
     *  石家庄商学院
     */
    private String sjzBusOrgId;
    /**
     * 合肥商学院
     */
    private String hfBusOrgId;
    
    /**
     * 商机盒子天津商学院
     */
    private Long sjhzTjBusOrgId;
    
}
