package com.findata.api.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class QueryPerformanceAspect {

    @Around("execution(* com.findata.api.repository..*(..))")
    public Object logQueryExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - startTime;

        String methodName = joinPoint.getSignature().toShortString();

        if (executionTime > 100) {
            log.warn("SLOW QUERY: {} took {}ms", methodName, executionTime);
        } else if (executionTime > 50) {
            log.info("Query: {} took {}ms", methodName, executionTime);
        } else {
            log.debug("Query: {} took {}ms", methodName, executionTime);
        }

        return result;
    }
}
