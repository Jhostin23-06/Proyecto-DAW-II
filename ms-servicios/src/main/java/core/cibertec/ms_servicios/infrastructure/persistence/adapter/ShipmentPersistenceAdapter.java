package core.cibertec.ms_servicios.infrastructure.persistence.adapter;

import core.cibertec.ms_servicios.application.port.outservice.ShipmentPersistencePort;
import core.cibertec.ms_servicios.domain.bean.ShipmentRequest;
import core.cibertec.ms_servicios.domain.bean.ShipmentResponse;
import core.cibertec.ms_servicios.infrastructure.persistence.entity.ShipmentEntity;
import core.cibertec.ms_servicios.infrastructure.persistence.repository.ShipmentRepository;
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
public class ShipmentPersistenceAdapter implements ShipmentPersistencePort {

    private final ShipmentRepository shipmentRepository;
    private final ModelMapper modelMapper;

    @Override
    public ShipmentResponse save(ShipmentRequest request) {
        try {
            // Manual mapping to avoid ModelMapper configuration issues
            ShipmentEntity entity = new ShipmentEntity();
            entity.setShipmentId(null);
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
            entity.setStatus("CREATED");
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());

            ShipmentEntity saved = shipmentRepository.save(entity);

            // Map entity -> response using ModelMapper (should be safe)
            return modelMapper.map(saved, ShipmentResponse.class);
        } catch (Exception e) {
            log.error("Error mapping/saving shipment: {}", e.toString(), e);
            throw new RuntimeException("Error al guardar shipment: " + e.getMessage(), e);
        }
    }

    @Override
    public ShipmentResponse findById(Long id) {
        return shipmentRepository.findById(id)
                .map(e -> modelMapper.map(e, ShipmentResponse.class))
                .orElse(null);
    }

    @Override
    public List<ShipmentResponse> findAll() {
        return shipmentRepository.findAll().stream()
                .map(e -> modelMapper.map(e, ShipmentResponse.class))
                .collect(Collectors.toList());
    }
}
