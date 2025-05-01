package com.edi.ediconvertor.service;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;
@Service
public class emailservice {

    @Autowired
    private JavaMailSender mailSender;
    public void sendEmailWithTableAndAttachments(JavaMailSender mailSender, String toEmail, String subject,  String htmlContent, List<File> attachments) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Set the recipient, subject and HTML content for the email body
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText( htmlContent, true);  // true means HTML content

            // Attach the XML files
            for (File file : attachments) {
                FileSystemResource resource = new FileSystemResource(file);
                helper.addAttachment(file.getName(), resource);
            }

            // Send the email
            mailSender.send(message);
            System.out.println("Email sent successfully with table and attachments!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    }
