package com.transporte.mstransportistas.application.service;

import com.transporte.mstransportistas.application.port.outservice.TransportPersistencePort;
import com.transporte.mstransportistas.application.port.usecase.DeleteTransportPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteTransportServiceImpl implements DeleteTransportPort {

    private final TransportPersistencePort transportPersistencePort;

    @Override
    public void deleteTransport(String id) {
        log.info("Eliminando (desactivando) transporte con ID: {}", id);

        // Verificar que existe
        transportPersistencePort.findById(id);

        // Desactivar (eliminación lógica)
        transportPersistencePort.deactivate(id);
        log.info("Transporte eliminado (desactivado) exitosamente: {}", id);
    }
}
