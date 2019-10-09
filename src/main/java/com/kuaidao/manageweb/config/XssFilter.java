package com.kuaidao.manageweb.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.MimeHeader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by wangxue on 2017/7/27.
 */
//@WebFilter(filterName="/CodeFilter",urlPatterns="/*")
public class XssFilter implements Filter {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private ArrayList<String> excludedPageArray = new ArrayList<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //excludedPageArray.add("/information/saveOrUpdate");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        boolean falg = false;
        for(String reU:excludedPageArray){
            if (reU.equals(req.getServletPath())){
                falg = true;
                break;
            }
        }
        
  /*      String method = req.getMethod();
        if("POST".equalsIgnoreCase(method)) {
            
        }*/
        if (falg){
            filterChain.doFilter(servletRequest,servletResponse);
        }else {
            XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper(req);
            filterChain.doFilter(xssRequest,servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
