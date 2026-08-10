package com.practice.spring.util.validator;

public interface Validator<T> {
    void validate(T obj);
}
