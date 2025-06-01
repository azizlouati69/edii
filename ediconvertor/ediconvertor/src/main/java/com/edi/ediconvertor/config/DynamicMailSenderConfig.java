package com.edi.ediconvertor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

@Configuration
public class DynamicMailSenderConfig {

    @Bean
    public JavaMailSender javaMailSender(EdiconvertorConfig config) {
        MailConfig mail = config.getMailConfig();

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mail.getHost());
        mailSender.setPort(mail.getPort());
        mailSender.setUsername(mail.getUsername());
        mailSender.setPassword(mail.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", String.valueOf(mail.getProperties().isAuth()));
        props.put("mail.smtp.starttls.enable", String.valueOf(mail.getProperties().isStarttls()));

        return mailSender;
    }
}
