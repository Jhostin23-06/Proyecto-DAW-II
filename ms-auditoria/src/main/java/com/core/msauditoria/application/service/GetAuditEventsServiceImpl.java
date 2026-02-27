package com.core.msauditoria.application.service;

import com.core.msauditoria.application.port.outservice.AuditEventPersistencePort;
import com.core.msauditoria.application.port.usecase.GetAuditEventsPort;
import com.core.msauditoria.domain.bean.AuditEventResponse;
import com.core.msauditoria.domain.exception.AuditEventNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class GetAuditEventsServiceImpl implements GetAuditEventsPort {

    private final AuditEventPersistencePort persistencePort;

    @Override
    public Page<AuditEventResponse> findEvents(String source,
                                               String eventType,
                                               String routingKey,
                                               String correlationId,
                                               Instant from,
                                               Instant to,
                                               int page,
                                               int size) {
        return persistencePort.findEvents(source, eventType, routingKey, correlationId, from, to, page, size);
    }

    @Override
    public AuditEventResponse findById(Long auditId) {
        return persistencePort.findById(auditId)
                .orElseThrow(() -> new AuditEventNotFoundException("Evento de auditoria no encontrado: " + auditId));
    }
}
