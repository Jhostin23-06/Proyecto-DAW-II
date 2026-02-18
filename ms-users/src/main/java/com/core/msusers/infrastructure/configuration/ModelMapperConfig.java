package com.core.msusers.infrastructure.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        // Configuraciones adicionales si las necesitas
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        return modelMapper;
    }

}
