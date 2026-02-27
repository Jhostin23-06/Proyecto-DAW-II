package core.cibertec.ms_servicios.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignPropagationConfig {

    private static final String AUTHORIZATION = "Authorization";

    @Bean
    public RequestInterceptor requestPropagationInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                String authHeader = request.getHeader(AUTHORIZATION);
                if (authHeader != null && !authHeader.isBlank()) {
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
        };
    }
}
