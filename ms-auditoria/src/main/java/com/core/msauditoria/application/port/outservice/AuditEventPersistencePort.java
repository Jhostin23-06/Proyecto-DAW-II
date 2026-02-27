package com.core.msauditoria.application.port.outservice;

import com.core.msauditoria.domain.bean.AuditEventRecordRequest;
import com.core.msauditoria.domain.bean.AuditEventResponse;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.util.Optional;

public interface AuditEventPersistencePort {
    AuditEventResponse save(AuditEventRecordRequest request);

    Page<AuditEventResponse> findEvents(String source,
                                        String eventType,
                                        String routingKey,
                                        String correlationId,
                                        Instant from,
                                        Instant to,
                                        int page,
                                        int size);

    Optional<AuditEventResponse> findById(Long auditId);
}
