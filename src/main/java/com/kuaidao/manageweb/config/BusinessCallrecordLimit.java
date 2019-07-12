package com.kuaidao.manageweb.config;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;


/**
 * 商学院通话录音 约束
 * @author  Devin.Chen
 * @date 2019-07-11 17:24:03
 * @version V1.0
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "callrecord.businessOrgId")
public class BusinessCallrecordLimit {
    /**
     * qhdBusOrgId
     */
    private Long qhdBusOrgId;
    /**
     * 郑州商学院
     */
    private Long zzBusOrgId;
    /**
     *  石家庄商学院
     */
    private Long sjzBusOrgId;
    /**
     * 合肥商学院
     */
    private Long hfBusOrgId;
    
    /**
     * 商机盒子天津商学院
     */
    private Long sjhzTjBusOrgId;
    
    private Set<Long> orgIdSet = new HashSet<>();
    
    @PostConstruct
    public void initSet() {
        addOrgId(qhdBusOrgId);
        addOrgId(zzBusOrgId);
        addOrgId(sjzBusOrgId);
        addOrgId(hfBusOrgId);
        addOrgId(sjhzTjBusOrgId);
    }
    
    public void addOrgId(Long orgId) {
        if(orgId!=null) {
            orgIdSet.add(orgId);
        }
    }

}
