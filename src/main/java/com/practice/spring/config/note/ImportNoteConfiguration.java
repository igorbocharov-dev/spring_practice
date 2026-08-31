package com.practice.spring.config.note;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@ConfigurationProperties(prefix = "import.note")
@Component
public class ImportNoteConfiguration {

    private Long limit;
}
