
package com.kuaidao.manageweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import com.kuaidao.common.util.Snowflake;
import com.kuaidao.manageweb.config.SnowflakeArgs;


@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableCircuitBreaker
public class ManageWebApplication 
{

    
    public static void main( String[] args )
    {
    	SpringApplication.run(ManageWebApplication.class, args);
    }
    
}
