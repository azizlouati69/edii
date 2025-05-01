package com.edi.ediconvertor;

import com.edi.ediconvertor.service.FileWatcherService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
 import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableAsync
@EnableDiscoveryClient
public class EdiconvertorApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdiconvertorApplication.class, args);
    }
    @Bean
    CommandLineRunner startWatching(FileWatcherService fileWatcherService) {
        return args -> fileWatcherService.watchDirectory();
    }
}
