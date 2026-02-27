package com.transporte.mstransportistas.application.service;

import com.transporte.mstransportistas.application.port.outservice.TransportEventPort;
import com.transporte.mstransportistas.application.port.outservice.TransportPersistencePort;
import com.transporte.mstransportistas.application.port.usecase.UpdateStatusPort;
import com.transporte.mstransportistas.domain.bean.TransportResponse;
import com.transporte.mstransportistas.domain.model.TransportModel;
import com.transporte.mstransportistas.domain.model.TransportStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateStatusServiceImpl implements UpdateStatusPort {

    private final TransportPersistencePort transportPersistencePort;
    private final TransportEventPort transportEventPort;
    private final TransportModel transportModel;

    @Override
    public TransportResponse updateTransportStatus(String id, String newStatusStr, String location, String reason) {
        log.info("Actualizando estado del transporte ID: {} a {}", id, newStatusStr);

        TransportResponse current = transportPersistencePort.findById(id);
        String currentStatusStr = current.getTransportStatus();

        // Soporte para actualizacion solo de ubicacion (sin cambio de estado).
        if (newStatusStr == null || newStatusStr.isBlank()) {
            if (location == null || location.isBlank()) {
                throw new IllegalArgumentException("location es requerida cuando no se envia transportStatus");
            }

            TransportResponse updated = transportPersistencePort.updateStatus(id, currentStatusStr, location);
            log.info("Ubicacion actualizada para transporte ID: {}", id);
            return updated;
        }

        TransportStatus currentStatus = TransportStatus.fromString(currentStatusStr);
        TransportStatus newStatus = TransportStatus.fromString(newStatusStr);

        if (!transportModel.validateTransition(currentStatus, newStatus)) {
            String msg = transportModel.getTransitionMessage(currentStatus, newStatus);
            throw new IllegalStateException(msg.isEmpty() ? "Transicion no permitida" : msg);
        }

        String normalizedNewStatus = newStatus.name();
        TransportResponse updated = transportPersistencePort.updateStatus(id, normalizedNewStatus, location);
        log.info("Estado actualizado exitosamente: {} -> {}", currentStatus, normalizedNewStatus);

        try {
            transportEventPort.publishTransportStatusChanged(
                    id,
                    current.getTransportUserId(),
                    currentStatusStr,
                    normalizedNewStatus,
                    reason
            );
            log.debug("Evento de cambio de estado publicado");
        } catch (Exception e) {
            log.error("Error al publicar evento de cambio de estado: {}", e.getMessage());
        }

        return updated;
    }
}
