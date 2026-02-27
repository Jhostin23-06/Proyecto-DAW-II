package core.cibertec.ms_servicios.infrastructure.outservice.adapter;

import core.cibertec.ms_servicios.application.port.outservice.ClientValidationPort;
import core.cibertec.ms_servicios.application.port.outservice.client.ClientFeignClient;
import core.cibertec.ms_servicios.domain.exception.ExternalServiceException;
import core.cibertec.ms_servicios.infrastructure.outservice.dto.ClientSummaryResponse;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientValidationAdapter implements ClientValidationPort {

    private static final String CB_NAME = "clientValidationService";
    private static final String RETRY_NAME = "clientValidationRetry";

    private final ClientFeignClient clientFeignClient;

    @Override
    @Retry(name = RETRY_NAME, fallbackMethod = "fallbackExistsById")
    @CircuitBreaker(name = CB_NAME, fallbackMethod = "fallbackExistsById")
    public boolean existsById(Long clientId) {
        try {
            List<ClientSummaryResponse> clients = clientFeignClient.getClients(clientId);
            return clients.stream().anyMatch(c -> c.id() != null && c.id().equals(clientId));
        } catch (FeignException.NotFound ex) {
            return false;
        } catch (Exception ex) {
            log.error("Error validating client id={} via feign: {}", clientId, ex.toString());
            throw new ExternalServiceException("No se pudo validar cliente en ms-clientes", ex);
        }
    }

    public boolean fallbackExistsById(Long clientId, Throwable t) {
        log.error("Circuit/retry fallback validating client id={}: {}", clientId, t.toString());
        throw new ExternalServiceException("No se pudo validar cliente en ms-clientes", t);
    }
}
