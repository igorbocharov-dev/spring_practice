package com.practice.spring.util.validator.note;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(value = "test")
public final class TestNoteLimitValidator implements NoteLimitValidator{
    @Override
    public void validate(Long obj) {

    }
}
