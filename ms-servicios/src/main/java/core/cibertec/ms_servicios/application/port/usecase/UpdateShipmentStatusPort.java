package core.cibertec.ms_servicios.application.port.usecase;

import core.cibertec.ms_servicios.domain.bean.ShipmentResponse;

public interface UpdateShipmentStatusPort {
    ShipmentResponse updateShipmentStatus(Long shipmentId, Long statusId);
}
