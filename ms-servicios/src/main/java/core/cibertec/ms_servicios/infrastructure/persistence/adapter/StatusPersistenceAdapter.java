package core.cibertec.ms_servicios.infrastructure.persistence.adapter;

import core.cibertec.ms_servicios.application.port.outservice.StatusPersistencePort;
import core.cibertec.ms_servicios.domain.bean.Status;
import core.cibertec.ms_servicios.infrastructure.persistence.entity.ShipmentStatusEntity;
import core.cibertec.ms_servicios.infrastructure.persistence.repository.ShipmentRepository;
import core.cibertec.ms_servicios.infrastructure.persistence.repository.ShipmentStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StatusPersistenceAdapter implements StatusPersistencePort {

    private final ShipmentStatusRepository shipmentStatusRepository;
    private final ShipmentRepository shipmentRepository;

    @Override
    public List<Status> findAllStatuses() {
        return shipmentStatusRepository.findAll().stream()
                .sorted(Comparator.comparing(ShipmentStatusEntity::getStatusId))
                .map(e -> new Status(e.getStatusId(), e.getStatusName()))
                .toList();
    }

    @Override
    public Status createStatus(String statusName) {
        shipmentStatusRepository.findByStatusNameIgnoreCase(statusName.trim())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Status already exists: " + existing.getStatusName());
                });

        long nextId = shipmentStatusRepository.findAll().stream()
                .mapToLong(ShipmentStatusEntity::getStatusId)
                .max()
                .orElse(0L) + 1L;

        ShipmentStatusEntity saved = shipmentStatusRepository.save(
                new ShipmentStatusEntity(nextId, statusName.trim().toUpperCase())
        );
        return new Status(saved.getStatusId(), saved.getStatusName());
    }

    @Override
    public boolean deleteById(Long statusId) {
        if (!shipmentStatusRepository.existsById(statusId)) {
            return false;
        }
        if (shipmentRepository.existsByStatusRef_StatusId(statusId)) {
            throw new IllegalStateException("Status is in use by one or more shipments");
        }
        shipmentStatusRepository.deleteById(statusId);
        return true;
    }
}
