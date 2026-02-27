package com.transporte.mstransportistas.infrastructure.outservice;

import com.transporte.mstransportistas.application.port.outservice.UserServicePort;
import com.transporte.mstransportistas.application.port.outservice.client.UserFeignClient;
import com.transporte.mstransportistas.domain.bean.UserInfo;
import com.transporte.mstransportistas.domain.exception.ExternalServiceException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceAdapter implements UserServicePort {

    private static final String CB_NAME = "userService";
    private static final String RETRY_NAME = "userServiceRetry";

    private final UserFeignClient userFeignClient;

    @Override
    @Retry(name = RETRY_NAME)
    @CircuitBreaker(name = CB_NAME, fallbackMethod = "fallbackGetUserById")
    public UserInfo getUserById(String userId) {
        if (userId == null || userId.isBlank()) {
            return null;
        }

        try {
            return userFeignClient.getUserById(userId);
        } catch (FeignException.NotFound ex) {
            return null;
        } catch (Exception ex) {
            throw new ExternalServiceException("No se pudo consultar usuario en ms-users", ex);
        }
    }

    public UserInfo fallbackGetUserById(String userId, Throwable t) {
        if (t instanceof IllegalArgumentException illegalArgumentException) {
            throw illegalArgumentException;
        }

        log.error("Circuit breaker activo para ms-users. userId={}, error={}", userId, t.toString());
        throw new ExternalServiceException("Servicio de usuarios no disponible temporalmente", t);
    }
}
