package ua.hodik.gym.aspect;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Log4j2
@Component
public class LogAspect {

    public static final String TRANSACTION_ID = "transactionId";

    @Before("execution(* ua.hodik.gym.controller.*.*(..))")
    public void logBeforeRestCall(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        log.debug("TransactionId: {}, Endpoint: {}, Request Params: {}",
                MDC.get(TRANSACTION_ID),
                joinPoint.getSignature().getName(),
                args != null && args.length > 0 ? args[0] : "No args");
    }

    @AfterReturning(value = "execution(* ua.hodik.gym.controller.*.*(..))", returning = "response")
    public void logAfterRestCall(JoinPoint joinPoint, ResponseEntity<?> response) {
        log.debug("TransactionId: {}, Endpoint: {}, Response Status: {}",
                MDC.get(TRANSACTION_ID),
                joinPoint.getSignature().getName(),
                response.getStatusCode());
    }
}
