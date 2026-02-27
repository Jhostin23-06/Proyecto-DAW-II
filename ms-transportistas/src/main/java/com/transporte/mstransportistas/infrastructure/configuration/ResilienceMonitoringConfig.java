package com.transporte.mstransportistas.infrastructure.configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ResilienceMonitoringConfig {

    private static final String USER_SERVICE_CB = "userService";
    private static final String USER_SERVICE_RETRY = "userServiceRetry";

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;

    @PostConstruct
    public void registerResilienceEventLogging() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(USER_SERVICE_CB);
        circuitBreaker.getEventPublisher()
                .onStateTransition(event -> log.warn(
                        "CB state transition. name={} from={} to={}",
                        event.getCircuitBreakerName(),
                        event.getStateTransition().getFromState(),
                        event.getStateTransition().getToState()))
                .onCallNotPermitted(event -> log.warn(
                        "CB call not permitted. name={} creationTime={}",
                        event.getCircuitBreakerName(),
                        event.getCreationTime()))
                .onError(event -> log.warn(
                        "CB call error. name={} elapsedMs={} errorType={}",
                        event.getCircuitBreakerName(),
                        event.getElapsedDuration().toMillis(),
                        event.getThrowable() == null ? "n/a" : event.getThrowable().getClass().getSimpleName()));

        Retry retry = retryRegistry.retry(USER_SERVICE_RETRY);
        retry.getEventPublisher()
                .onRetry(event -> log.warn(
                        "Retry attempt. name={} attempts={} lastErrorType={}",
                        event.getName(),
                        event.getNumberOfRetryAttempts(),
                        event.getLastThrowable() == null ? "n/a" : event.getLastThrowable().getClass().getSimpleName()))
                .onError(event -> log.error(
                        "Retry exhausted with error. name={} attempts={} errorType={}",
                        event.getName(),
                        event.getNumberOfRetryAttempts(),
                        event.getLastThrowable() == null ? "n/a" : event.getLastThrowable().getClass().getSimpleName()));
    }
}
