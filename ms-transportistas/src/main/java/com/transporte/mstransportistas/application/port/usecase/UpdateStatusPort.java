package com.transporte.mstransportistas.application.port.usecase;

import com.transporte.mstransportistas.domain.bean.TransportResponse;
import org.springframework.stereotype.Component;

@Component
public interface UpdateStatusPort {
    TransportResponse updateTransportStatus(Long id, String newStatus, String location, String reason);
}
