package com.core.msnotificaciones.domain.bean;

import java.time.Instant;

public record NotificationResponse(
        Long notificationId,
        String eventSource,
        String eventType,
        String title,
        String message,
        String payload,
        String correlationId,
        String targetUserId,
        Boolean read,
        Instant occurredAt,
        Instant receivedAt,
        Instant readAt
) {
}
