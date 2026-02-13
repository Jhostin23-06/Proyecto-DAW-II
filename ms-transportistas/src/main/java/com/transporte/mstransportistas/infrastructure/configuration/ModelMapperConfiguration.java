package com.transporte.mstransportistas.infrastructure.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Configuraciones personalizadas si son necesarias
        modelMapper.getConfiguration()
                .setSkipNullEnabled(true);

        return modelMapper;
    }

}
