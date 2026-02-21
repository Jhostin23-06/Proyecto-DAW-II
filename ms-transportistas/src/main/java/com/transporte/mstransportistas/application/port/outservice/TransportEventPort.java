package com.transporte.mstransportistas.application.port.outservice;

import com.transporte.mstransportistas.domain.bean.TransportResponse;
import org.springframework.stereotype.Component;

@Component
public interface TransportEventPort {
    void publishTransportCreated(TransportResponse response);
    void publishTransportStatusChanged(String transportId, String oldStatus,
                                       String newStatus, String reason);
    void publishTransportAssigned(String transportId, Long shipmentId);
}
