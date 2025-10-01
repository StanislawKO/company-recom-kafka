package com.stas_kozh.recommendationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CompanyNotificationConfig {

    @Bean
    RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
