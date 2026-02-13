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
    public TransportResponse updateTransportStatus(Long id, String newStatusStr, String location, String reason) {
        log.info("Actualizando estado del transporte ID: {} a {}", id, newStatusStr);

        // Obtener transporte actual
        TransportResponse current = transportPersistencePort.findById(id);
        if (current == null) {
            throw new RuntimeException("Transporte no encontrado con ID: " + id);
        }

        // Convertir y validar estados
        TransportStatus currentStatus = TransportStatus.fromString(current.getTransportStatus());
        TransportStatus newStatusS = TransportStatus.fromString(newStatusStr);

        // Validar transición usando el modelo
        if (!transportModel.validateTransition(currentStatus, newStatusS)) {
            String msg = transportModel.getTransitionMessage(currentStatus, newStatusS);
            throw new IllegalStateException(msg.isEmpty() ? "Transición no permitida" : msg);
        }

        // Actualizar estado
        TransportResponse updated = transportPersistencePort.updateStatus(id, newStatusStr, location);
        log.info("Estado actualizado exitosamente: {} -> {}", currentStatus, newStatusStr);

        // Publicar evento
        try {
            transportEventPort.publishTransportStatusChanged(
                    id,
                    current.getTransportStatus(),
                    newStatusStr,
                    reason
            );
            log.debug("Evento de cambio de estado publicado");
        } catch (Exception e) {
            log.error("Error al publicar evento de cambio de estado: {}", e.getMessage());
        }

        return updated;
    }
}
