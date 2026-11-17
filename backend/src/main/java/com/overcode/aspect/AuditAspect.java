package com.overcode.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;

@Aspect
@Component
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) && execution(public * *(..))")
    public void restControllerMethods() {
    }

    @Around("restControllerMethods()")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {
        Instant timestamp = Instant.now();
        String user = resolveUser();
        String method = joinPoint.getSignature().toShortString();
        String params = Arrays.toString(joinPoint.getArgs());
        long start = System.currentTimeMillis();

        try {
            return joinPoint.proceed();
        } finally {
            long executionTime = System.currentTimeMillis() - start;
            log.info("<{}, {}, {}, {}, {}ms>", timestamp, user, method, params, executionTime);
        }
    }

    private String resolveUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null || "anonymousUser".equalsIgnoreCase(authentication.getName())) {
            return "ANONYMOUS";
        }
        return authentication.getName();
    }
}
