package com.kuaidao.manageweb.config;

import com.kuaidao.manageweb.EmailSend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Created by fengyixuan on 2018/11/30
 */
@Configuration
public class EmailSendConfig {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.from}")
    private String fromMail;


    @Bean
    public EmailSend emailSend(){
        return new EmailSend(this.javaMailSender,fromMail);
    }
}
