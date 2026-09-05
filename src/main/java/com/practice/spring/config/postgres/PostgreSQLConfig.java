package com.practice.spring.config.postgres;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class PostgreSQLConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    DataSourceProperties postgresDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    DataSource postgresDataSource(
            @Qualifier("postgresDataSourceProperties")
            DataSourceProperties properties) {

        return properties.initializeDataSourceBuilder().build();
    }
}
