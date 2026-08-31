package com.practice.spring.util.validator.note;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public final class ProdNoteLimitValidator implements NoteLimitValidator{

    @Override
    public void validate(Long currentCountOfNotes) {

    }
}
