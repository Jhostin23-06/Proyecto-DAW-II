package core.cibertec.ms_servicios.application.port.outservice;

import core.cibertec.ms_servicios.domain.bean.ShipmentResponse;

public interface ShipmentEventPort {
    void publishShipmentCreated(ShipmentResponse response);
}
