package com.practice.spring.service.document.task;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "document.task")
@Getter
@Setter
public class DocumentTaskProperties {

    private int batchUpdateLimit;
}
