package com.transporte.mstransportistas.application.service;

import com.transporte.mstransportistas.application.port.outservice.TransportPersistencePort;
import com.transporte.mstransportistas.application.port.usecase.GetTransportPort;
import com.transporte.mstransportistas.domain.bean.TransportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetTransportServiceImpl implements GetTransportPort {

    private final TransportPersistencePort transportPersistencePort;

    @Override
    public TransportResponse getTransportById(String id) {
        log.debug("Obteniendo transporte por ID: {}", id);
        return transportPersistencePort.findById(id);
    }

    @Override
    public List<TransportResponse> getAllTransports() {
        log.debug("Getting all transports");
        return transportPersistencePort.findAll();
    }

    @Override
    public List<TransportResponse> getTransportsByStatus(String status) {
        log.debug("Obteniendo transportes por estado: {}", status);
        return transportPersistencePort.findByStatus(status);
    }

    @Override
    public List<TransportResponse> getAvailableTransports() {
        log.debug("Obteniendo transportes disponibles");
        return transportPersistencePort.findAvailableTransports();
    }

    @Override
    public List<TransportResponse> getTransportsByUserId(String userId) {
        log.debug("Obteniendo transportes por usuario ID: {}", userId);
        return transportPersistencePort.findByUserId(userId);
    }
}
