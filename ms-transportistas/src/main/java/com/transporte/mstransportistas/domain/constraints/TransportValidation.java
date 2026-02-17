package com.transporte.mstransportistas.domain.constraints;

import com.transporte.mstransportistas.domain.bean.TransportRequest;

public interface TransportValidation {
    boolean validateForCreation(TransportRequest request);
    boolean validateForUpdate(TransportRequest request);
    boolean validateLicensePlate(String licensePlate);
    boolean validateCapacity(Double capacity);
}
