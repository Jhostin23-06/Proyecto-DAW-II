package com.transporte.mstransportistas.infrastructure.configuration;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor tokenPropagationInterceptor() {
        return new TokenPropagationInterceptor();
    }

}
