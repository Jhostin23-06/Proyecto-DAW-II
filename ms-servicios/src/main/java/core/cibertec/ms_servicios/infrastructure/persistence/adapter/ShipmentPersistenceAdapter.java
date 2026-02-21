package core.cibertec.ms_servicios.infrastructure.persistence.adapter;

import core.cibertec.ms_servicios.application.port.outservice.ShipmentPersistencePort;
import core.cibertec.ms_servicios.domain.bean.ShipmentRequest;
import core.cibertec.ms_servicios.domain.bean.ShipmentResponse;
import core.cibertec.ms_servicios.infrastructure.persistence.entity.ShipmentEntity;
import core.cibertec.ms_servicios.infrastructure.persistence.entity.ShipmentStatusEntity;
import core.cibertec.ms_servicios.infrastructure.persistence.repository.ShipmentCategoryRepository;
import core.cibertec.ms_servicios.infrastructure.persistence.repository.ShipmentRepository;
import core.cibertec.ms_servicios.infrastructure.persistence.repository.ShipmentStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShipmentPersistenceAdapter implements ShipmentPersistencePort {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentStatusRepository shipmentStatusRepository;
    private final ShipmentCategoryRepository shipmentCategoryRepository;

    @Override
    public ShipmentResponse save(ShipmentRequest request) {
        try {
            // Manual mapping to avoid ModelMapper configuration issues
            ShipmentEntity entity = new ShipmentEntity();
            entity.setShipmentId(null);
            if (!shipmentCategoryRepository.existsById(request.getCategoryId())) {
                throw new IllegalArgumentException("Category not found with id=" + request.getCategoryId());
            }
            entity.setCategoryId(request.getCategoryId());
            entity.setDescription(request.getDescription());
            entity.setPrice(request.getPrice());
            entity.setWeight(request.getWeight());
            entity.setVolume(request.getVolume());
            entity.setOrigin(request.getOrigin());
            entity.setDestination(request.getDestination());
            entity.setClientId(request.getClientId());
            entity.setTransportId(request.getTransportId());
            entity.setOrderNumber(request.getOrderNumber());
            entity.setAtDate(request.getAtDate());

            ShipmentStatusEntity createdStatus = shipmentStatusRepository.findById(1L)
                    .or(() -> shipmentStatusRepository.findByStatusNameIgnoreCase("CREATED"))
                    .orElseThrow(() -> new IllegalStateException("Status CREATED not found in catalog"));
            entity.setStatusRef(createdStatus);
            entity.setStatus(createdStatus.getStatusName());

            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());

            ShipmentEntity saved = shipmentRepository.save(entity);
            return toResponse(saved);
        } catch (Exception e) {
            log.error("Error mapping/saving shipment: {}", e.toString(), e);
            throw new RuntimeException("Error al guardar shipment: " + e.getMessage(), e);
        }
    }

    @Override
    public ShipmentResponse findById(Long id) {
        return shipmentRepository.findById(id)
                .map(this::toResponse)
                .orElse(null);
    }

    @Override
    public List<ShipmentResponse> findAll() {
        return shipmentRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShipmentResponse> findByTransportId(String transportId) {
        return shipmentRepository.findByTransportId(transportId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShipmentResponse> findByTransportIds(List<String> transportIds) {
        if (transportIds == null || transportIds.isEmpty()) {
            return Collections.emptyList();
        }
        return shipmentRepository.findByTransportIdIn(transportIds).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ShipmentResponse updateStatus(Long shipmentId, Long statusId) {
        Optional<ShipmentStatusEntity> nextStatusOpt = shipmentStatusRepository.findById(statusId);
        if (nextStatusOpt.isEmpty()) {
            return null;
        }

        ShipmentStatusEntity nextStatus = nextStatusOpt.get();
        return shipmentRepository.findById(shipmentId)
                .map(entity -> {
                    entity.setStatusRef(nextStatus);
                    entity.setStatus(nextStatus.getStatusName());
                    entity.setUpdatedAt(LocalDateTime.now());
                    ShipmentEntity saved = shipmentRepository.save(entity);
                    return toResponse(saved);
                })
                .orElse(null);
    }

    @Override
    public boolean deleteById(Long shipmentId) {
        if (!shipmentRepository.existsById(shipmentId)) {
            return false;
        }
        shipmentRepository.deleteById(shipmentId);
        return true;
    }

    private ShipmentResponse toResponse(ShipmentEntity entity) {
        ShipmentResponse resp = new ShipmentResponse();
        resp.setShipmentId(entity.getShipmentId());
        resp.setCategoryId(entity.getCategoryId());
        resp.setDescription(entity.getDescription());
        resp.setPrice(entity.getPrice());
        resp.setWeight(entity.getWeight());
        resp.setVolume(entity.getVolume());
        resp.setOrigin(entity.getOrigin());
        resp.setDestination(entity.getDestination());
        resp.setClientId(entity.getClientId());
        resp.setTransportId(entity.getTransportId());
        resp.setOrderNumber(entity.getOrderNumber());
        resp.setAtDate(entity.getAtDate());
        resp.setCreatedAt(entity.getCreatedAt());
        resp.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getStatusRef() != null) {
            resp.setStatusId(entity.getStatusRef().getStatusId());
            resp.setStatus(entity.getStatusRef().getStatusName());
        } else {
            resp.setStatus(entity.getStatus());
        }
        return resp;
    }
}
