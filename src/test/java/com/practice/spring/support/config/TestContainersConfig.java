package com.practice.spring.support.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.clickhouse.ClickHouseContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfig {

    @Bean
    static PostgreSQLContainer postgresContainer() {
        return new PostgreSQLContainer("postgres:17");
    }

    @Bean
    DynamicPropertyRegistrar postgresProperties(PostgreSQLContainer container) {
        return registry -> {
            registry.add("spring.datasource.url", container::getJdbcUrl);
            registry.add("spring.datasource.username", container::getUsername);
            registry.add("spring.datasource.password", container::getPassword);
        };
    }

    @Bean
    static ClickHouseContainer clickHouseContainer() {
        return new ClickHouseContainer("clickhouse/clickhouse-server:26.8");
    }

    @Bean
    DynamicPropertyRegistrar clickHouseProperties(ClickHouseContainer container) {
        return registry -> {
            registry.add("clickhouse.url", container::getJdbcUrl);
            registry.add("clickhouse.username", container::getUsername);
            registry.add("clickhouse.password", container::getPassword);
        };
    }

    @Bean
    @ServiceConnection
    static MongoDBContainer mongoContainer() {
        return new MongoDBContainer("mongo:8.0");
    }

    @Bean
    @ServiceConnection
    static KafkaContainer kafkaContainer(){
        return new KafkaContainer("apache/kafka:4.3.1");
    }
}