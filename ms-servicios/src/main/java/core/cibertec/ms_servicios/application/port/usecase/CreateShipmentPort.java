package core.cibertec.ms_servicios.application.port.usecase;

import core.cibertec.ms_servicios.domain.bean.ShipmentRequest;
import core.cibertec.ms_servicios.domain.bean.ShipmentResponse;

public interface CreateShipmentPort {
    ShipmentResponse createShipment(ShipmentRequest request);
}
