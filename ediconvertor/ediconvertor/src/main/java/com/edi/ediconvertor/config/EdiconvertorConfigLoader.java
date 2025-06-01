package com.edi.ediconvertor.config;

import jakarta.xml.bind.JAXBContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.File;
 // <-- this disables this config when 'test' profile is active

@Configuration
public class EdiconvertorConfigLoader {

    @Bean
    public EdiconvertorConfig ediconvertorConfig() throws Exception {
        JAXBContext context = JAXBContext.newInstance(EdiconvertorConfig.class);

        // Load from external file path
        File configFile = new File("convertor-config.xml"); // Make sure this file exists next to the .exe or launcher

        if (!configFile.exists()) {
            throw new IllegalArgumentException("convertor-config.xml not found at expected location: " + configFile.getAbsolutePath());
        }

        return (EdiconvertorConfig) context.createUnmarshaller().unmarshal(configFile);
    }
}
