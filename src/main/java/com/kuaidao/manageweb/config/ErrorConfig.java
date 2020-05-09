package com.kuaidao.manageweb.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ErrorConfig implements ErrorPageRegistrar {

 @Override
 public void registerErrorPages(ErrorPageRegistry registry) {
     ErrorPage error400Page = new ErrorPage(HttpStatus.BAD_REQUEST, "/error-400.html");
     ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/error/error404");
     ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error-500.html");
     registry.addErrorPages(error400Page,error404Page,error500Page);
 }
}