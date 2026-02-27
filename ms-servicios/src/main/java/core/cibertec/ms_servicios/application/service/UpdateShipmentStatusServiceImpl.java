package core.cibertec.ms_servicios.application.service;

import core.cibertec.ms_servicios.application.port.outservice.ShipmentEventPort;
import core.cibertec.ms_servicios.application.port.outservice.ShipmentPersistencePort;
import core.cibertec.ms_servicios.application.port.usecase.UpdateShipmentStatusPort;
import core.cibertec.ms_servicios.domain.bean.ShipmentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateShipmentStatusServiceImpl implements UpdateShipmentStatusPort {

    private final ShipmentPersistencePort shipmentPersistencePort;
    private final ShipmentEventPort shipmentEventPort;

    @Override
    public ShipmentResponse updateShipmentStatus(Long shipmentId, Long statusId) {
        ShipmentResponse updated = shipmentPersistencePort.updateStatus(shipmentId, statusId);

        try {
            shipmentEventPort.publishShipmentStatusUpdated(updated);
        } catch (Exception ex) {
            log.error("Failed to publish shipment status updated event shipmentId={}", shipmentId, ex);
        }

        return updated;
    }
}
