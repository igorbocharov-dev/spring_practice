package com.practice.spring.config.note;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Setter
@Getter
@ConfigurationProperties(prefix = "note")
@Component
@Profile(value = "dev")
public class NoteConfiguration {

    private Long limit;

}
