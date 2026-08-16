package com.practice.spring.config.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter notesCreatedCounter(MeterRegistry meterRegistry){
        return Counter.builder("notes.created")
                .description("Number of created notes")
                .register(meterRegistry);
    }
}
