package com.core.msauditoria.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "audit_events")
public class AuditEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Long auditId;

    @Column(name = "event_source", nullable = false, length = 80)
    private String eventSource;

    @Column(name = "event_type", nullable = false, length = 150)
    private String eventType;

    @Column(name = "routing_key", length = 150)
    private String routingKey;

    @Column(name = "exchange_name", length = 150)
    private String exchangeName;

    @Column(name = "queue_name", length = 150)
    private String queueName;

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "consumed_at", nullable = false)
    private Instant consumedAt;
}
