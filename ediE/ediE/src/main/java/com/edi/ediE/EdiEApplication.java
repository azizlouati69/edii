package com.edi.ediE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
@EntityScan("entities.model")

@EnableDiscoveryClient
@SpringBootApplication
@EnableJpaRepositories("repository")
public class EdiEApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdiEApplication.class, args);
	}

}
