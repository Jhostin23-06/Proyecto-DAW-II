package core.cibertec.ms_servicios.infrastructure.outservice.adapter;

import core.cibertec.ms_servicios.application.port.outservice.ClientValidationPort;
import core.cibertec.ms_servicios.application.port.outservice.client.ClientFeignClient;
import core.cibertec.ms_servicios.infrastructure.outservice.dto.ClientSummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientValidationAdapter implements ClientValidationPort {

    private final ClientFeignClient clientFeignClient;

    @Override
    public boolean existsById(Long clientId) {
        try {
            List<ClientSummaryResponse> clients = clientFeignClient.getClients(clientId);
            return clients.stream().anyMatch(c -> c.id() != null && c.id().equals(clientId));
        } catch (Exception ex) {
            log.error("Error validating client id={} via feign: {}", clientId, ex.toString());
            return false;
        }
    }
}
