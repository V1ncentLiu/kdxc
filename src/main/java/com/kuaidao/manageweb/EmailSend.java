package com.kuaidao.manageweb;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.internet.MimeUtility;
import java.io.File;
import java.util.List;

/**
 * Created by fengyixuan on 2018/11/30
 */
public class EmailSend {

    private JavaMailSender javaMailSender;

    private String from ;

    public EmailSend() {
    }

    public EmailSend(JavaMailSender javaMailSender, String from) {
        this.javaMailSender = javaMailSender;
        this.from = from;
    }

    public JavaMailSender getJavaMailSender() {
        return javaMailSender;
    }
    public void setJavaMailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /**
     * 发送不含附件的邮件
     * @param subject 主题
     * @param content 内容
     * @param targets 接收者
     */
    public void sendEmail(String subject, String content, String... targets){
        sendEmail(null,subject,content,targets);
    }

    /**
     * 发送邮件
     * @param attachmentFile 附件
     * @param subject 主题
     * @param content 内容
     * @param targets 接收者
     */
    public void sendEmail(File attachmentFile, String subject, String content, String... targets){
            MimeMessagePreparator preparator = (mimeMessage -> {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            if(attachmentFile!=null){
                mimeMessageHelper = new MimeMessageHelper(mimeMessage, true,"utf-8");
                mimeMessageHelper.addAttachment(MimeUtility.encodeWord(attachmentFile.getName(),"utf-8","B"),attachmentFile);
            }
            mimeMessageHelper.setSubject(subject);
            if(targets==null || targets.length==0){
                return;
            }else{
                mimeMessageHelper.setTo(targets);
            }
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setText(content, true);
        });
        this.javaMailSender.send(preparator);
    }



    /**
     * @Description:
     * @date: 2019/5/15 17:36
     * @param: attachmentFiles
     * @Param: subject
     * @Param: content
     * @Param: from
     * @Param: targets
     * @return: void
     */
    public void sendEmailFiles(List<File> attachmentFiles, String subject, String content, String... targets){
            MimeMessagePreparator preparator = (mimeMessage -> {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            if(attachmentFiles!=null){
                mimeMessageHelper = new MimeMessageHelper(mimeMessage, true,"utf-8");
                for (File attachmentFile : attachmentFiles) {
                    mimeMessageHelper.addAttachment(MimeUtility.encodeWord(attachmentFile.getName(),"utf-8","B"),attachmentFile);
                }
            }
            mimeMessageHelper.setSubject(subject);
            if(targets==null || targets.length==0){
                return;
            }else{
                mimeMessageHelper.setTo(targets);
            }
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setText(content, true);
        });
        this.javaMailSender.send(preparator);
    }
}
