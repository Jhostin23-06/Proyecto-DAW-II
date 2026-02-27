package com.core.msauditoria.application.port.usecase;

import com.core.msauditoria.domain.bean.AuditEventResponse;
import org.springframework.data.domain.Page;

import java.time.Instant;

public interface GetAuditEventsPort {

    Page<AuditEventResponse> findEvents(String source,
                                        String eventType,
                                        String routingKey,
                                        String correlationId,
                                        Instant from,
                                        Instant to,
                                        int page,
                                        int size);

    AuditEventResponse findById(Long auditId);
}
