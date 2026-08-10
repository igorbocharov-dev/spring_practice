package com.practice.spring.util.validator;

import org.springframework.stereotype.Component;

@Component("IdValidator")
public final class IdValidator implements Validator<Long>{

    @Override
    public void validate(Long id) {
        if(id == null) throw new IllegalArgumentException("id не должен быть null");
        if(id <= 0) throw new IllegalArgumentException("id должен быть положительным");
    }
}
