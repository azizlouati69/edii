package com.example.ftp_listener;

import com.example.ftp_listener.service.FtpPollingService;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
 @SpringBootApplication

@EnableScheduling  // This enables scheduling in Spring Boot
public class FtpListenerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FtpListenerApplication.class, args);
	}
}