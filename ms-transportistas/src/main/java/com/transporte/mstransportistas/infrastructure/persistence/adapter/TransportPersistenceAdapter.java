package com.transporte.mstransportistas.infrastructure.persistence.adapter;

import com.transporte.mstransportistas.application.port.outservice.TransportPersistencePort;
import com.transporte.mstransportistas.domain.bean.TransportRequest;
import com.transporte.mstransportistas.domain.bean.TransportResponse;
import com.transporte.mstransportistas.domain.exception.TransportNotFoundException;
import com.transporte.mstransportistas.infrastructure.persistence.entity.TransportEntity;
import com.transporte.mstransportistas.infrastructure.persistence.repository.TransportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransportPersistenceAdapter implements TransportPersistencePort {

    private final TransportRepository transportRepository;
    private final ModelMapper modelMapper;

    @Override
    public TransportResponse save(TransportRequest request) {
        log.info(">>> [PERSISTENCE ADAPTER] Intentando guardar transporte con placa: {}", request.getTransportLicensePlate());
        try {
            // 1. Mapeo
            log.debug("Mapeando request a entity...");
            TransportEntity entity = modelMapper.map(request, TransportEntity.class);
            entity.setTransportId(null);
            log.debug("Entity mapeada: {}", entity);

            // 2. Setear campos adicionales
            entity.setTransportStatus("AVAILABLE");
            entity.setActive(true);
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());

            // 3. Guardar
            log.debug("Llamando a repository.save...");
            TransportEntity saved = transportRepository.save(entity);
            log.info(">>> Transporte guardado exitosamente con ID: {}", saved.getTransportId());

            // 4. Convertir a response
            return toResponse(saved);
        } catch (Exception e) {
            log.error(">>> ERROR en persistencia: {}", e.getMessage(), e);
            throw new RuntimeException("Error al guardar transporte", e);
        }
    }

    @Override
    public TransportResponse findById(String id) {
        return transportRepository.findById(id)
                .filter(TransportEntity::getActive)
                .map(this::toResponse)
                .orElseThrow(() -> new TransportNotFoundException("Transporte no encontrado con ID: " + id));
    }

    @Override
    public List<TransportResponse> findAll() {
        return transportRepository.findByActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }


    @Override
    public List<TransportResponse> findByStatus(String status) {
        return transportRepository.findByTransportStatusAndActiveTrue(status).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransportResponse> findAvailableTransports() {
        return transportRepository.findAvailableTransports().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransportResponse> findByUserId(String userId) {
        return transportRepository.findByTransportUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TransportResponse updateStatus(String id, String status, String location) {
        TransportEntity entity = transportRepository.findById(id)
                .orElseThrow(() -> new TransportNotFoundException("Transporte no encontrado con ID: " + id));

        entity.setTransportStatus(status);
        if (location != null && !location.trim().isEmpty()) {
            entity.setTransportLocation(location);
        }
        entity.setUpdatedAt(LocalDateTime.now());

        TransportEntity updated = transportRepository.save(entity);
        return toResponse(updated);
    }

    @Override
    public boolean existsByLicensePlate(String licensePlate) {
        return transportRepository.existsByTransportLicensePlate(licensePlate);
    }

    @Override
    public void deactivate(String id) {
        TransportEntity entity = transportRepository.findById(id)
                .orElseThrow(() -> new TransportNotFoundException("Transporte no encontrado con ID: " + id));
        entity.setActive(false);
        entity.setUpdatedAt(LocalDateTime.now());
        transportRepository.save(entity);
    }

    @Override
    public TransportResponse update(String id, TransportRequest request) {
        TransportEntity entity = transportRepository.findById(id)
                .orElseThrow(() -> new TransportNotFoundException("Transporte no encontrado con ID: " + id));

        if (request.getTransportType() != null) {
            entity.setTransportType(request.getTransportType());
        }
        if (request.getTransportCapacity() != null) {
            entity.setTransportCapacity(request.getTransportCapacity());
        }
        if (request.getTransportLocation() != null) {
            entity.setTransportLocation(request.getTransportLocation());
        }
        if (request.getTransportDriver() != null) {
            entity.setTransportDriver(request.getTransportDriver());
        }
        if (request.getTransportCompany() != null) {
            entity.setTransportCompany(request.getTransportCompany());
        }

        if (request.getTransportUserId() != null) {
            entity.setTransportUserId(request.getTransportUserId());
        }

        entity.setUpdatedAt(LocalDateTime.now());
        TransportEntity updated = transportRepository.save(entity);
        return toResponse(updated);
    }

    @Override
    public boolean existsById(String id) {
        return transportRepository.existsById(id);
    }

    @Override
    public List<TransportResponse> findSuitableTransports(Double requiredCapacity, String location) {
        List<TransportEntity> entities;

        if (requiredCapacity != null && location != null && !location.trim().isEmpty()) {
            // Filtrar por capacidad y ubicación
            entities = transportRepository.findAvailableTransportsWithCapacity(requiredCapacity)
                    .stream()
                    .filter(entity -> entity.getTransportLocation() != null &&
                            entity.getTransportLocation().toLowerCase()
                                    .contains(location.toLowerCase()))
                    .collect(Collectors.toList());
        } else if (requiredCapacity != null) {
            // Solo filtrar por capacidad
            entities = transportRepository.findAvailableTransportsWithCapacity(requiredCapacity);
        } else {
            // Solo transportes disponibles
            entities = transportRepository.findAvailableTransports();
        }

        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private TransportResponse toResponse(TransportEntity entity) {
        TransportResponse response = modelMapper.map(entity, TransportResponse.class);

        // Calcular si está disponible
        boolean isAvailable = "AVAILABLE".equals(entity.getTransportStatus()) &&
                Boolean.TRUE.equals(entity.getActive());
        response.setAvailable(isAvailable);

        return response;
    }
}
