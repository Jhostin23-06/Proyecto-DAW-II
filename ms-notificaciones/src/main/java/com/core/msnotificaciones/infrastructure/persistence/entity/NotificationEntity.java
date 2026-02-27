package com.core.msnotificaciones.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notifications_event_source", columnList = "event_source"),
        @Index(name = "idx_notifications_is_read", columnList = "is_read"),
        @Index(name = "idx_notifications_received_at", columnList = "received_at"),
        @Index(name = "idx_notifications_target_user_id", columnList = "target_user_id")
})
@Getter
@Setter
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @Column(name = "event_source", nullable = false, length = 80)
    private String eventSource;

    @Column(name = "event_type", nullable = false, length = 120)
    private String eventType;

    @Column(name = "routing_key", length = 120)
    private String routingKey;

    @Column(name = "exchange_name", length = 120)
    private String exchangeName;

    @Column(name = "queue_name", length = 120)
    private String queueName;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "message_text", nullable = false, length = 500)
    private String message;

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Column(name = "correlation_id", length = 120)
    private String correlationId;

    @Column(name = "target_user_id", length = 120)
    private String targetUserId;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    @Column(name = "is_read", nullable = false)
    private Boolean read;

    @Column(name = "read_at")
    private Instant readAt;
}
