package com.transporte.mstransportistas.application.port.usecase;

import com.transporte.mstransportistas.domain.bean.TransportRequest;
import com.transporte.mstransportistas.domain.bean.TransportResponse;
import org.springframework.stereotype.Component;

@Component
public interface CreateTransportPort {
    TransportResponse createTransport(TransportRequest request);
}
