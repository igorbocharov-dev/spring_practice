package com.practice.spring.context;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

@Component
@RequestScope
public class RequestContext {

    private String requestId;

    public String getRequestId(){
        if(requestId == null){
            requestId = UUID.randomUUID().toString();
        }
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
