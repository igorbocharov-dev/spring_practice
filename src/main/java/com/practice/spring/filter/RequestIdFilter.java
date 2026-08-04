package com.practice.spring.filter;

import com.practice.spring.context.RequestContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.practice.spring.util.requestHeaders.RequestHeaders.X_REQUEST_ID;

@Component
public class RequestIdFilter extends OncePerRequestFilter {

    private final RequestContext requestContext;

    @Autowired
    public RequestIdFilter(RequestContext requestContext) {
        this.requestContext = requestContext;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String requestId = request.getHeader(X_REQUEST_ID);

            if(requestId == null || requestId.isBlank()){
                requestId = requestContext.getRequestId();
            } else {
                requestContext.setRequestId(requestId);
            }

            MDC.put("requestId", requestId);
            response.setHeader(X_REQUEST_ID, requestId);
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
