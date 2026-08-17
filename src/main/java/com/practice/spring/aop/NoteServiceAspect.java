package com.practice.spring.aop;

import com.practice.spring.context.RequestContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.ScopeNotActiveException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class NoteServiceAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoteServiceAspect.class);

    private final RequestContext requestContext;

    @Autowired
    public NoteServiceAspect(RequestContext requestContext) {
        this.requestContext = requestContext;
    }

    @Pointcut("execution(public * com.practice.spring.service.note.NoteService+.* (..))")
    private void anyPublicMethods(){}

    @Around("anyPublicMethods()")
    public Object loggingNoteService(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        long timeToBefore = System.currentTimeMillis();

        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - timeToBefore;
            LOGGER.info("NoteService.{} took {} ms, requestId={}", methodName, duration, getRequestId());
        }
    }

    private String getRequestId(){
        try {
            return requestContext.getRequestId();
        } catch (ScopeNotActiveException e) {
            return "N/A";
        }
    }
}
