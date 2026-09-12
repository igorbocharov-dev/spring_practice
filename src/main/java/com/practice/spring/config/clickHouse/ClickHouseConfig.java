package com.practice.spring.config.clickHouse;

import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

import javax.sql.DataSource;

@Configuration
public class ClickHouseConfig {

    @Bean
    @ConfigurationProperties("clickhouse")
    DataSourceProperties clickHouseDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    DataSource clickHouseDataSource() {
        var hikari = clickHouseDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        hikari.setMaximumPoolSize(5);
        return hikari;
    }

    @Bean
    JdbcClient clickHouseJdbcClient(@Qualifier("clickHouseDataSource") DataSource dataSource) {
        return JdbcClient.create(dataSource);
    }

    @Bean
    Flyway clickHouseFlyway(
            @Qualifier("clickHouseDataSource") DataSource dataSource) {

        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/clickhouse")
                .load();
    }

    @Bean
    FlywayMigrationInitializer clickHouseFlywayInitializer(
            @Qualifier("clickHouseFlyway") Flyway flyway) {

        return new FlywayMigrationInitializer(flyway);
    }
}
