package com.practice.spring.util.requestHeaders;

import org.springframework.stereotype.Component;

@Component
public final class RequestHeaders {

    public static final String X_REQUEST_ID = "X-Request-ID";

    private RequestHeaders(){}
}
