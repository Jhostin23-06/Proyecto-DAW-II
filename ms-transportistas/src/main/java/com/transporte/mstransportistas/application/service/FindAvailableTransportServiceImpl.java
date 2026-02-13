package com.transporte.mstransportistas.application.service;

import com.transporte.mstransportistas.application.port.outservice.TransportPersistencePort;
import com.transporte.mstransportistas.application.port.usecase.FindAvailableTransportPort;
import com.transporte.mstransportistas.domain.bean.TransportResponse;
import com.transporte.mstransportistas.domain.model.TransportModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindAvailableTransportServiceImpl implements FindAvailableTransportPort {

    private final TransportPersistencePort transportPersistencePort;
    private final TransportModel transportModel;

    @Override
    public List<TransportResponse> findAvailableTransports(Double requiredCapacity, String location) {
        log.info("Buscando transportes disponibles. Capacidad requerida: {}, Ubicación: {}",
                requiredCapacity, location);

        // 1. Validar capacidad requerida
        if (requiredCapacity == null || requiredCapacity <= 0) {
            throw new IllegalArgumentException("La capacidad requerida debe ser mayor que 0");
        }

        // 2. Obtener todos los transportes disponibles
        List<TransportResponse> availableTransports = transportPersistencePort.findAvailableTransports();

        // 3. Filtrar por capacidad y ubicación
        return availableTransports.stream()
                .filter(transport -> hasSufficientCapacity(transport, requiredCapacity))
                .filter(transport -> isNearLocation(transport, location))
                .sorted(this::compareBySuitability)
                .collect(Collectors.toList());
    }

    private boolean hasSufficientCapacity(TransportResponse transport, Double requiredCapacity) {
        return transport.getTransportCapacity() != null &&
                transport.getTransportCapacity() >= requiredCapacity;
    }

    private boolean isNearLocation(TransportResponse transport, String targetLocation) {
        if (targetLocation == null || targetLocation.trim().isEmpty()) {
            return true; // No se especificó ubicación, aceptar todos
        }

        if (transport.getTransportLocation() == null) {
            return false;
        }

        // Lógica simple de proximidad (en producción usarías geolocalización)
        String transportLocation = transport.getTransportLocation().toLowerCase().trim();
        String target = targetLocation.toLowerCase().trim();

        return transportLocation.contains(target) || target.contains(transportLocation);
    }

    private int compareBySuitability(TransportResponse t1, TransportResponse t2) {
        // Ordenar por:
        // 1. Capacidad más cercana a la requerida
        // 2. Más recientemente actualizado
        // 3. ID más bajo

        // Comparar por capacidad (la que tenga capacidad más cercana primero)
        if (t1.getTransportCapacity() != null && t2.getTransportCapacity() != null) {
            Double diff1 = Math.abs(t1.getTransportCapacity() - 0); // Aquí pondrías la capacidad requerida
            Double diff2 = Math.abs(t2.getTransportCapacity() - 0);
            int capacityComparison = Double.compare(diff1, diff2);
            if (capacityComparison != 0) {
                return capacityComparison;
            }
        }

        // Comparar por fecha de actualización (más reciente primero)
        if (t1.getUpdatedAt() != null && t2.getUpdatedAt() != null) {
            int dateComparison = t2.getUpdatedAt().compareTo(t1.getUpdatedAt());
            if (dateComparison != 0) {
                return dateComparison;
            }
        }

        // Comparar por ID (más bajo primero)
        return Long.compare(t1.getTransportId(), t2.getTransportId());
    }
}
