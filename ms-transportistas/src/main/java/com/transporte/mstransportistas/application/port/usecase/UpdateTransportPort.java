package com.transporte.mstransportistas.application.port.usecase;

import com.transporte.mstransportistas.domain.bean.TransportRequest;
import com.transporte.mstransportistas.domain.bean.TransportResponse;
import org.springframework.stereotype.Component;

@Component
public interface UpdateTransportPort {
    TransportResponse updateTransport(String id, TransportRequest request);
    TransportResponse assignToUser(String id, String userId);
    void deactivateTransport(String id);
}
