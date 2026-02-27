package com.transporte.mstransportistas.infrastructure.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class TokenPropagationInterceptor implements RequestInterceptor {
    private static final String AUTHORIZATION = "Authorization";

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            String authHeader = request.getHeader(AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                requestTemplate.header(AUTHORIZATION, authHeader);
            }

            String correlationFromRequest = request.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER);
            if (correlationFromRequest != null && !correlationFromRequest.isBlank()) {
                requestTemplate.header(CorrelationIdFilter.CORRELATION_ID_HEADER, correlationFromRequest);
                return;
            }
        }

        String correlationFromMdc = MDC.get(CorrelationIdFilter.MDC_KEY);
        if (correlationFromMdc != null && !correlationFromMdc.isBlank()) {
            requestTemplate.header(CorrelationIdFilter.CORRELATION_ID_HEADER, correlationFromMdc);
        }
    }
}
