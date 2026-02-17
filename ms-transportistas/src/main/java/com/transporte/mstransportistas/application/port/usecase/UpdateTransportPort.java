package com.transporte.mstransportistas.application.port.usecase;

import com.transporte.mstransportistas.domain.bean.TransportRequest;
import com.transporte.mstransportistas.domain.bean.TransportResponse;
import org.springframework.stereotype.Component;

@Component
public interface UpdateTransportPort {
    TransportResponse updateTransport(Long id, TransportRequest request);
    void deactivateTransport(Long id);
}
