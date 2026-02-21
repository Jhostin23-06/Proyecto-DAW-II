package core.cibertec.ms_servicios.application.port.usecase;

import core.cibertec.ms_servicios.domain.bean.ShipmentResponse;

import java.util.List;

public interface GetShipmentPort {
    ShipmentResponse getShipmentById(Long id);
    List<ShipmentResponse> getAllShipments();
}
