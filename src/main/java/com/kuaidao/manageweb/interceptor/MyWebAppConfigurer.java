package com.kuaidao.manageweb.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@Configuration
public class MyWebAppConfigurer
        extends WebMvcConfigurerAdapter {

    /**
     * 下载excel 模板时，定位到 resources 目录下
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/excel-templates/**").addResourceLocations("classpath:/excel-templates/");
        super.addResourceHandlers(registry);
    }

}
