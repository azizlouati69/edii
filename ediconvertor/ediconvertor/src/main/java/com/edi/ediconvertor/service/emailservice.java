package com.edi.ediconvertor.service;

import com.edi.ediconvertor.config.EdiconvertorConfig;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class emailservice {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EdiconvertorConfig config;

    public void sendEmailWithTableAndAttachments(String toEmail, String subject, String htmlContent, List<File> attachments) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);



            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML content

            for (File file : attachments) {
                FileSystemResource resource = new FileSystemResource(file);
                helper.addAttachment(file.getName(), resource);
            }

            mailSender.send(message);
            System.out.println("Email sent successfully to " + toEmail + " with table and attachments!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
