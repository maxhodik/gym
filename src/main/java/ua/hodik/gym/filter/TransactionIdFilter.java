package ua.hodik.gym.filter;


import jakarta.servlet.*;
import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Log4j2
@Component
public class TransactionIdFilter implements Filter {


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        MDC.put("transactionId", UUID.randomUUID().toString());
        log.info("Intercept coming request and set into MDC context transactionId");
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
