package com.transporte.mstransportistas.application.service;

import com.transporte.mstransportistas.application.port.outservice.TransportPersistencePort;
import com.transporte.mstransportistas.application.port.usecase.UpdateTransportPort;
import com.transporte.mstransportistas.domain.bean.TransportRequest;
import com.transporte.mstransportistas.domain.bean.TransportResponse;
import com.transporte.mstransportistas.domain.model.TransportModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateTransportServiceImpl implements UpdateTransportPort {

    private final TransportPersistencePort transportPersistencePort;
    private final TransportModel transportModel;

    @Override
    public TransportResponse updateTransport(Long id, TransportRequest request) {
        log.info("Actualizando transporte con ID: {}", id);

        // 1. Validar datos de actualización
        transportModel.validateForUpdate(request);

        // 2. Verificar que el transporte existe
        TransportResponse existing = transportPersistencePort.findById(id);
        if (existing == null) {
            throw new RuntimeException("Transporte no encontrado con ID: " + id);
        }

        // 3. Si se está actualizando la placa, verificar que no exista otra igual
        if (request.getTransportLicensePlate() != null &&
                !request.getTransportLicensePlate().equals(existing.getTransportLicensePlate())) {

            if (transportPersistencePort.existsByLicensePlate(request.getTransportLicensePlate())) {
                throw new IllegalArgumentException(
                        String.format("La placa %s ya existe en el sistema",
                                request.getTransportLicensePlate())
                );
            }
        }

        // 4. Actualizar transporte
        TransportResponse updated = transportPersistencePort.update(id, request);
        log.info("Transporte actualizado exitosamente: {}", id);

        return updated;
    }

    @Override
    public void deactivateTransport(Long id) {
        log.info("Desactivando transporte con ID: {}", id);

        // 1. Verificar que existe
        TransportResponse existing = transportPersistencePort.findById(id);
        if (existing == null) {
            throw new RuntimeException("Transporte no encontrado con ID: " + id);
        }

        // 2. Desactivar
        transportPersistencePort.deactivate(id);
        log.info("Transporte desactivado exitosamente: {}", id);
    }
}
