package com.example.patients.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for ModelMapper
 * Provides mapping configuration between Domain entities and DTOs
 */
@Configuration
public class MapperConfig {
    
    /**
     * Configure ModelMapper bean with custom settings
     * @return ModelMapper instance configured for the application
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        
        // Configurar estrategia de matching
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT) // Solo mapea campos exactos
                .setFieldMatchingEnabled(true) // Permite mapear campos privados
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE) // Acceso a campos privados
                .setSkipNullEnabled(true); // No mapea valores null
        
        return mapper;
    }
}