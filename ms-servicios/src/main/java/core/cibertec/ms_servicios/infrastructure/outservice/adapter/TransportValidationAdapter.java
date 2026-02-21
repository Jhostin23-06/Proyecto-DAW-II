package core.cibertec.ms_servicios.infrastructure.outservice.adapter;

import core.cibertec.ms_servicios.application.port.outservice.TransportValidationPort;
import core.cibertec.ms_servicios.application.port.outservice.client.TransportFeignClient;
import core.cibertec.ms_servicios.infrastructure.outservice.dto.TransportSummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransportValidationAdapter implements TransportValidationPort {

    private final TransportFeignClient transportFeignClient;

    @Override
    public boolean existsById(String transportId) {
        if (transportId == null || transportId.isBlank()) {
            return false;
        }
        try {
            TransportSummaryResponse transport = transportFeignClient.getTransportById(transportId);
            return transport != null && transport.transportId() != null;
        } catch (Exception ex) {
            log.error("Error validating transport id={} via feign: {}", transportId, ex.toString());
            return false;
        }
    }

    @Override
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
            return Collections.emptyList();
        }
    }
}
