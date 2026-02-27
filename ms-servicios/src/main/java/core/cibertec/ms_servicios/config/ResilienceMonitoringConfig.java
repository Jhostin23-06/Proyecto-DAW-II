package core.cibertec.ms_servicios.config;

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

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;

    @PostConstruct
    public void registerEventLogging() {
        registerCircuitBreaker("shipmentService");
        registerCircuitBreaker("clientValidationService");
        registerCircuitBreaker("transportValidationService");

        registerRetry("shipmentServiceRetry");
        registerRetry("clientValidationRetry");
        registerRetry("transportValidationRetry");
    }

    private void registerCircuitBreaker(String name) {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker(name);
        cb.getEventPublisher()
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
    }

    private void registerRetry(String name) {
        Retry retry = retryRegistry.retry(name);
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
