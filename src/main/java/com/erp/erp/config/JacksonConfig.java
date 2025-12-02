package com.erp.erp.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {
    
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Registrar módulo para fechas Java 8+
        mapper.registerModule(new JavaTimeModule());
        
        // Configuraciones generales
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        
        // ⭐ IMPORTANTE: Deshabilitar detección automática de ciclos infinitos
        mapper.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
        
        // ⭐ NUEVO: Configurar límite de profundidad más alto (solución temporal)
        mapper.getFactory().setStreamReadConstraints(
            mapper.getFactory().streamReadConstraints()
                .rebuild()
                .maxNestingDepth(2000)
                .build()
        );
        
        mapper.getFactory().setStreamWriteConstraints(
            mapper.getFactory().streamWriteConstraints()
                .rebuild()
                .maxNestingDepth(2000)
                .build()
        );
        
        return mapper;
    }
    
    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        builder.featuresToDisable(SerializationFeature.FAIL_ON_SELF_REFERENCES);
        return builder;
    }
}