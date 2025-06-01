package com.example.ftp_listener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.xml.bind.JAXBContext;
import org.springframework.context.annotation.Profile;

import java.io.File;
@Profile("!test")  // <-- this disables this config when 'test' profile is active

@Configuration
public class FtpConfigLoader {

    @Bean
    public FtpConfig ftpConfig() throws Exception {
        JAXBContext context = JAXBContext.newInstance(FtpConfig.class);

        // Load from external file
        File configFile = new File("ftp-config.xml"); // Ensure this file exists next to your .exe or launcher

        if (!configFile.exists()) {
            throw new IllegalArgumentException("ftp-config.xml not found at expected location: " + configFile.getAbsolutePath());
        }

        return (FtpConfig) context.createUnmarshaller().unmarshal(configFile);
    }
}
