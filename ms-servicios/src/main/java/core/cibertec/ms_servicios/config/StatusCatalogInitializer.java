package core.cibertec.ms_servicios.config;

import core.cibertec.ms_servicios.infrastructure.persistence.entity.ShipmentStatusEntity;
import core.cibertec.ms_servicios.infrastructure.persistence.repository.ShipmentRepository;
import core.cibertec.ms_servicios.infrastructure.persistence.repository.ShipmentStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Locale;

@Configuration
@RequiredArgsConstructor
public class StatusCatalogInitializer {

    private final ShipmentStatusRepository shipmentStatusRepository;
    private final ShipmentRepository shipmentRepository;

    @Bean
    CommandLineRunner initStatusCatalog() {
        return args -> {
            List<ShipmentStatusEntity> defaults = List.of(
                    new ShipmentStatusEntity(1L, "CREATED"),
                    new ShipmentStatusEntity(2L, "ASSIGNED"),
                    new ShipmentStatusEntity(3L, "IN_TRANSIT"),
                    new ShipmentStatusEntity(4L, "DELIVERED"),
                    new ShipmentStatusEntity(5L, "CANCELLED")
            );

            for (ShipmentStatusEntity status : defaults) {
                shipmentStatusRepository.findById(status.getStatusId())
                        .or(() -> shipmentStatusRepository.findByStatusNameIgnoreCase(status.getStatusName()))
                        .orElseGet(() -> shipmentStatusRepository.save(status));
            }

            ShipmentStatusEntity createdDefault = shipmentStatusRepository.findById(1L)
                    .orElseThrow(() -> new IllegalStateException("Default CREATED status not found"));

            // Backfill status_id for legacy rows that only have text status.
            shipmentRepository.findAll().forEach(shipment -> {
                if (shipment.getStatusRef() == null) {
                    ShipmentStatusEntity mappedStatus = null;
                    if (shipment.getStatus() != null && !shipment.getStatus().isBlank()) {
                        mappedStatus = shipmentStatusRepository
                                .findByStatusNameIgnoreCase(shipment.getStatus().trim().toUpperCase(Locale.ROOT))
                                .orElse(null);
                    }
                    if (mappedStatus == null) {
                        mappedStatus = createdDefault;
                        shipment.setStatus(mappedStatus.getStatusName());
                    }
                    shipment.setStatusRef(mappedStatus);
                    shipmentRepository.save(shipment);
                }
            });
        };
    }
}
