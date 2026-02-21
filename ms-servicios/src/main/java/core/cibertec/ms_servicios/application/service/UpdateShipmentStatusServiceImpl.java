package core.cibertec.ms_servicios.application.service;

import core.cibertec.ms_servicios.application.port.outservice.ShipmentPersistencePort;
import core.cibertec.ms_servicios.application.port.usecase.UpdateShipmentStatusPort;
import core.cibertec.ms_servicios.domain.bean.ShipmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateShipmentStatusServiceImpl implements UpdateShipmentStatusPort {

    private final ShipmentPersistencePort shipmentPersistencePort;

    @Override
    public ShipmentResponse updateShipmentStatus(Long shipmentId, Long statusId) {
        return shipmentPersistencePort.updateStatus(shipmentId, statusId);
    }
}
