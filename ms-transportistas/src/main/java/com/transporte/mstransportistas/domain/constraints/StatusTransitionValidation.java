package com.transporte.mstransportistas.domain.constraints;

import com.transporte.mstransportistas.domain.model.TransportStatus;

public interface StatusTransitionValidation {
    boolean validateTransition(TransportStatus currentStatus, TransportStatus newStatus);
    String getTransitionMessage(TransportStatus currentStatus, TransportStatus newStatus);
}
