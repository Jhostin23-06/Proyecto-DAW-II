package com.transporte.mstransportistas.application.port.outservice;

import com.transporte.mstransportistas.domain.bean.TransportRequest;
import com.transporte.mstransportistas.domain.bean.TransportResponse;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public interface TransportPersistencePort {
    TransportResponse save(TransportRequest request);
    TransportResponse findById(String id);
    List<TransportResponse> findAll();
    List<TransportResponse> findByStatus(String status);
    List<TransportResponse> findAvailableTransports();
    List<TransportResponse> findByUserId(String userId);
    TransportResponse updateStatus(String id, String status, String location);
    boolean existsByLicensePlate(String licensePlate);
    void deactivate(String id);
    TransportResponse update(String id, TransportRequest request);
    boolean existsById(String id);
    List<TransportResponse> findSuitableTransports(Double requiredCapacity, String location);
}
