package core.cibertec.ms_servicios.infrastructure.outservice.adapter;

import core.cibertec.ms_servicios.application.port.outservice.TransportValidationPort;
import core.cibertec.ms_servicios.application.port.outservice.client.TransportFeignClient;
import core.cibertec.ms_servicios.domain.exception.ExternalServiceException;
import core.cibertec.ms_servicios.infrastructure.outservice.dto.TransportSummaryResponse;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransportValidationAdapter implements TransportValidationPort {

    private static final String CB_NAME = "transportValidationService";
    private static final String RETRY_NAME = "transportValidationRetry";

    private final TransportFeignClient transportFeignClient;

    @Override
    @Retry(name = RETRY_NAME, fallbackMethod = "fallbackExistsById")
    @CircuitBreaker(name = CB_NAME, fallbackMethod = "fallbackExistsById")
    public boolean existsById(String transportId) {
        if (transportId == null || transportId.isBlank()) {
            return false;
        }
        try {
            TransportSummaryResponse transport = transportFeignClient.getTransportById(transportId);
            return transport != null && transport.transportId() != null;
        } catch (FeignException.NotFound ex) {
            return false;
        } catch (Exception ex) {
            log.error("Error validating transport id={} via feign: {}", transportId, ex.toString());
            throw new ExternalServiceException("No se pudo validar transporte en ms-transportistas", ex);
        }
    }

    @Override
    @Retry(name = RETRY_NAME, fallbackMethod = "fallbackIsAvailableById")
    @CircuitBreaker(name = CB_NAME, fallbackMethod = "fallbackIsAvailableById")
    public boolean isAvailableById(String transportId) {
        if (transportId == null || transportId.isBlank()) {
            return false;
        }
        try {
            TransportSummaryResponse transport = transportFeignClient.getTransportById(transportId);
            if (transport == null || transport.transportId() == null) {
                return false;
            }
            if (transport.available() != null) {
                return transport.available();
            }
            return transport.transportStatus() != null
                    && "AVAILABLE".equalsIgnoreCase(transport.transportStatus());
        } catch (FeignException.NotFound ex) {
            return false;
        } catch (Exception ex) {
            log.error("Error validating transport availability id={} via feign: {}", transportId, ex.toString());
            throw new ExternalServiceException("No se pudo validar disponibilidad de transporte", ex);
        }
    }

    @Override
    @Retry(name = RETRY_NAME, fallbackMethod = "fallbackGetStatusById")
    @CircuitBreaker(name = CB_NAME, fallbackMethod = "fallbackGetStatusById")
    public Optional<String> getStatusById(String transportId) {
        if (transportId == null || transportId.isBlank()) {
            return Optional.empty();
        }
        try {
            TransportSummaryResponse transport = transportFeignClient.getTransportById(transportId);
            if (transport == null || transport.transportId() == null) {
                return Optional.empty();
            }
            String status = transport.transportStatus();
            return status == null || status.isBlank() ? Optional.empty() : Optional.of(status);
        } catch (FeignException.NotFound ex) {
            return Optional.empty();
        } catch (Exception ex) {
            log.error("Error getting transport status id={} via feign: {}", transportId, ex.toString());
            throw new ExternalServiceException("No se pudo obtener estado del transporte", ex);
        }
    }

    @Override
    @Retry(name = RETRY_NAME, fallbackMethod = "fallbackFindTransportIdsByUserId")
    @CircuitBreaker(name = CB_NAME, fallbackMethod = "fallbackFindTransportIdsByUserId")
    public List<String> findTransportIdsByUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            return Collections.emptyList();
        }

        try {
            return transportFeignClient.getTransportsByUserId(userId).stream()
                    .map(TransportSummaryResponse::transportId)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (Exception ex) {
            log.error("Error getting transports by userId={} via feign: {}", userId, ex.toString());
            throw new ExternalServiceException("No se pudo consultar transportes por usuario", ex);
        }
    }

    public boolean fallbackExistsById(String transportId, Throwable t) {
        log.error("Circuit/retry fallback validating transport id={}: {}", transportId, t.toString());
        throw new ExternalServiceException("No se pudo validar transporte en ms-transportistas", t);
    }

    public boolean fallbackIsAvailableById(String transportId, Throwable t) {
        log.error("Circuit/retry fallback validating transport availability id={}: {}", transportId, t.toString());
        throw new ExternalServiceException("No se pudo validar disponibilidad de transporte", t);
    }

    public Optional<String> fallbackGetStatusById(String transportId, Throwable t) {
        log.error("Circuit/retry fallback getting transport status id={}: {}", transportId, t.toString());
        throw new ExternalServiceException("No se pudo obtener estado del transporte", t);
    }

    public List<String> fallbackFindTransportIdsByUserId(String userId, Throwable t) {
        log.error("Circuit/retry fallback finding transports by userId={}: {}", userId, t.toString());
        throw new ExternalServiceException("No se pudo consultar transportes por usuario", t);
    }
}
