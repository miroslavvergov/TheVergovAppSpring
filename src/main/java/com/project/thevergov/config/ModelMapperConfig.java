package com.project.thevergov.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for ModelMapper.
 *
 * This class provides a bean definition for ModelMapper, allowing it to be
 * injected wherever needed in the application.
 */
@Configuration
public class ModelMapperConfig {

    /**
     * Creates a ModelMapper bean.
     *
     * @return a new instance of ModelMapper
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
