package core.cibertec.ms_servicios.application.port.outservice;

import core.cibertec.ms_servicios.domain.bean.ShipmentRequest;
import core.cibertec.ms_servicios.domain.bean.ShipmentResponse;

import java.util.List;

public interface ShipmentPersistencePort {
    ShipmentResponse save(ShipmentRequest request);
    ShipmentResponse findById(Long id);
    List<ShipmentResponse> findAll();
}
