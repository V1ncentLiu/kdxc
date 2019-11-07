package com.kuaidao.manageweb.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

/**
 * Created by wangxue on 2017/7/27.
 */
@Configuration
public class XssFilterConfig {
    @Bean
    public FilterRegistrationBean someFilterRegistration() {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(AuthFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

    /*使用annotation tag来取代<bean></bean>*/
    @Bean
    public Filter AuthFilter() {
        return new XssFilter();
    }
}
