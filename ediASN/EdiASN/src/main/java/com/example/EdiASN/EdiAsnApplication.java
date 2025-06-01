package com.example.EdiASN;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class EdiAsnApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdiAsnApplication.class, args);
	}

}
