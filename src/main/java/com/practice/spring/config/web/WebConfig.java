package com.practice.spring.config.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.removeIf(httpMessageConverter ->
                httpMessageConverter.getClass().getSimpleName().contains("Xml")
        );
        WebMvcConfigurer.super.configureMessageConverters(converters);
    }

}
