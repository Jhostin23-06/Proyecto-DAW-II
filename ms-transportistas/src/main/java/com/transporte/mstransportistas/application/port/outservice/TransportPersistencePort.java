package com.transporte.mstransportistas.application.port.outservice;

import com.transporte.mstransportistas.domain.bean.TransportRequest;
import com.transporte.mstransportistas.domain.bean.TransportResponse;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public interface TransportPersistencePort {
    TransportResponse save(TransportRequest request);
    TransportResponse findById(Long id);
    List<TransportResponse> findAll();
    List<TransportResponse> findByStatus(String status);
    List<TransportResponse> findAvailableTransports();
    List<TransportResponse> findByUserId(Long userId);
    TransportResponse updateStatus(Long id, String status, String location);
    boolean existsByLicensePlate(String licensePlate);
    void deactivate(Long id);
    TransportResponse update(Long id, TransportRequest request);
    boolean existsById(Long id);
    List<TransportResponse> findSuitableTransports(Double requiredCapacity, String location);
}
