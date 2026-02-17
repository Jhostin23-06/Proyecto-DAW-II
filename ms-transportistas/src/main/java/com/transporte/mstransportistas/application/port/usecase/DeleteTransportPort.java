package com.transporte.mstransportistas.application.port.usecase;

import org.springframework.stereotype.Component;

@Component
public interface DeleteTransportPort {
    void deleteTransport(Long id);
}
