package com.core.msnotificaciones.infrastructure.outservice;

import com.core.msnotificaciones.infrastructure.outservice.client.TransportFeignClient;
import com.core.msnotificaciones.infrastructure.outservice.dto.TransportSummaryResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransportUserResolver {

    private final TransportFeignClient transportFeignClient;

    public String resolveTransportUserId(String transportId) {
        if (transportId == null || transportId.isBlank()) {
            return null;
        }
        try {
            TransportSummaryResponse transport = transportFeignClient.getTransportById(transportId);
            if (transport == null || transport.transportUserId() == null || transport.transportUserId().isBlank()) {
                return null;
            }
            return transport.transportUserId();
        } catch (FeignException.NotFound ex) {
            return null;
        } catch (Exception ex) {
            log.warn("No se pudo resolver transportUserId para transportId={}: {}", transportId, ex.toString());
            return null;
        }
    }
}
