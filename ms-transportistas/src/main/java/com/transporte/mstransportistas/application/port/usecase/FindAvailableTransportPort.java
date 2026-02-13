package com.transporte.mstransportistas.application.port.usecase;

import com.transporte.mstransportistas.domain.bean.TransportResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface FindAvailableTransportPort {
    List<TransportResponse> findAvailableTransports(Double requiredCapacity, String location);
}
