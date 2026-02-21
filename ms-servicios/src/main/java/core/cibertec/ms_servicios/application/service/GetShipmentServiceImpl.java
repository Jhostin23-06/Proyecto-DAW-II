package core.cibertec.ms_servicios.application.service;

import core.cibertec.ms_servicios.application.port.outservice.ShipmentPersistencePort;
import core.cibertec.ms_servicios.application.port.usecase.GetShipmentPort;
import core.cibertec.ms_servicios.domain.bean.ShipmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetShipmentServiceImpl implements GetShipmentPort {

    private final ShipmentPersistencePort shipmentPersistencePort;

    @Override
    public ShipmentResponse getShipmentById(Long id) {
        return shipmentPersistencePort.findById(id);
    }

    @Override
    public List<ShipmentResponse> getAllShipments() {
        return shipmentPersistencePort.findAll();
    }
}
