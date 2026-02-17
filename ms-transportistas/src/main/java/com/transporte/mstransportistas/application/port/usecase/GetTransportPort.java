package com.transporte.mstransportistas.application.port.usecase;

import com.transporte.mstransportistas.domain.bean.TransportResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface GetTransportPort {
    TransportResponse getTransportById(Long id);
    List<TransportResponse> getAllTransports();
    List<TransportResponse> getTransportsByStatus(String status);
    List<TransportResponse> getAvailableTransports();
    List<TransportResponse> getTransportsByUserId(Long userId);
}
