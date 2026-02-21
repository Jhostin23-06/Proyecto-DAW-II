package core.cibertec.ms_servicios.application.service;

import core.cibertec.ms_servicios.application.port.outservice.ShipmentPersistencePort;
import core.cibertec.ms_servicios.application.port.usecase.DeleteShipmentPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteShipmentServiceImpl implements DeleteShipmentPort {

    private final ShipmentPersistencePort shipmentPersistencePort;

    @Override
    public boolean deleteShipment(Long shipmentId) {
        return shipmentPersistencePort.deleteById(shipmentId);
    }
}
