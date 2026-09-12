package com.practice.spring.config.mongodb;

import com.practice.spring.entity.document.IdDocumentEntityCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

    @Bean
    public IdDocumentEntityCallback registerCallback(){
        return new IdDocumentEntityCallback();
    }
}
