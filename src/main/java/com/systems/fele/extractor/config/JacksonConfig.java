package com.systems.fele.extractor.config;

import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.systems.fele.invoices.entity.Installment;
import com.systems.fele.invoices.entity.Installment.InstallmentSerializer;

public class JacksonConfig {
     @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        SimpleModule module = new SimpleModule();

        module.addSerializer(Installment.class, new InstallmentSerializer());

        mapper.registerModule(module);        
        return mapper;
    }
}
