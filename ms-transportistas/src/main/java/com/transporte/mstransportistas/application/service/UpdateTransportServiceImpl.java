package com.transporte.mstransportistas.application.service;

import com.transporte.mstransportistas.application.port.outservice.TransportPersistencePort;
import com.transporte.mstransportistas.application.port.outservice.TransportEventPort;
import com.transporte.mstransportistas.application.port.outservice.UserServicePort;
import com.transporte.mstransportistas.application.port.usecase.UpdateTransportPort;
import com.transporte.mstransportistas.domain.bean.TransportRequest;
import com.transporte.mstransportistas.domain.bean.TransportResponse;
import com.transporte.mstransportistas.domain.bean.UserInfo;
import com.transporte.mstransportistas.domain.model.TransportModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateTransportServiceImpl implements UpdateTransportPort {

    private final TransportPersistencePort transportPersistencePort;
    private final TransportEventPort transportEventPort;
    private final TransportModel transportModel;
    private final UserServicePort userServicePort;

    @Override
    public TransportResponse updateTransport(String id, TransportRequest request) {
        log.info("Actualizando transporte con ID: {}", id);

        transportModel.validateForUpdate(request);

        TransportResponse existing = transportPersistencePort.findById(id);
        if (request.getTransportLicensePlate() != null
                && !request.getTransportLicensePlate().equals(existing.getTransportLicensePlate())
                && transportPersistencePort.existsByLicensePlate(request.getTransportLicensePlate())) {
            throw new IllegalArgumentException(
                    String.format("La placa %s ya existe en el sistema", request.getTransportLicensePlate())
            );
        }

        TransportResponse updated = transportPersistencePort.update(id, request);
        log.info("Transporte actualizado exitosamente: {}", id);
        return updated;
    }

    @Override
    public TransportResponse assignToUser(String id, String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("userId es requerido para asignar transporte");
        }

        TransportResponse existing = transportPersistencePort.findById(id);
        boolean available = Boolean.TRUE.equals(existing.getAvailable());
        if (!available) {
            throw new IllegalStateException(
                    String.format("El transporte %s no esta disponible para asignacion", id)
            );
        }

        validateTransportUser(userId.trim());

        TransportRequest request = new TransportRequest();
        request.setTransportUserId(userId.trim());
        TransportResponse updated = transportPersistencePort.update(id, request);

        try {
            transportEventPort.publishTransportAssigned(id, userId.trim());
        } catch (Exception ex) {
            log.error("No se pudo publicar evento de asignacion transportista transportId={}: {}",
                    id, ex.getMessage());
        }

        return updated;
    }

    @Override
    public void deactivateTransport(String id) {
        log.info("Desactivando transporte con ID: {}", id);
        transportPersistencePort.findById(id);
        transportPersistencePort.deactivate(id);
        log.info("Transporte desactivado exitosamente: {}", id);
    }

    private void validateTransportUser(String userId) {
        UserInfo user = userServicePort.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("El usuario con ID " + userId + " no existe");
        }
        if (!"TRANSPORTER".equalsIgnoreCase(user.getUserRole())) {
            throw new IllegalArgumentException("El usuario no tiene rol de transportista");
        }
        if (!user.isActive()) {
            throw new IllegalArgumentException("El usuario no esta activo");
        }
    }
}
