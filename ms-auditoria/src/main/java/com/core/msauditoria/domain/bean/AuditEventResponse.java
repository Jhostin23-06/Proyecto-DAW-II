package com.core.msauditoria.domain.bean;

import java.time.Instant;

public record AuditEventResponse(
        Long auditId,
        String eventSource,
        String eventType,
        String routingKey,
        String exchangeName,
        String queueName,
        String correlationId,
        String payload,
        Instant occurredAt,
        Instant consumedAt
) {
}
