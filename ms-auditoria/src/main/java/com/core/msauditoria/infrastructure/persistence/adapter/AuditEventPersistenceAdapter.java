package com.core.msauditoria.infrastructure.persistence.adapter;

import com.core.msauditoria.application.port.outservice.AuditEventPersistencePort;
import com.core.msauditoria.domain.bean.AuditEventRecordRequest;
import com.core.msauditoria.domain.bean.AuditEventResponse;
import com.core.msauditoria.infrastructure.persistence.entity.AuditEventEntity;
import com.core.msauditoria.infrastructure.persistence.repository.AuditEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuditEventPersistenceAdapter implements AuditEventPersistencePort {

    private final AuditEventRepository repository;

    @Override
    public AuditEventResponse save(AuditEventRecordRequest request) {
        AuditEventEntity entity = new AuditEventEntity();
        entity.setEventSource(request.eventSource());
        entity.setEventType(request.eventType());
        entity.setRoutingKey(request.routingKey());
        entity.setExchangeName(request.exchangeName());
        entity.setQueueName(request.queueName());
        entity.setCorrelationId(request.correlationId());
        entity.setPayload(request.payload());
        entity.setOccurredAt(request.occurredAt() == null ? Instant.now() : request.occurredAt());
        entity.setConsumedAt(Instant.now());

        return toResponse(repository.save(entity));
    }

    @Override
    public Page<AuditEventResponse> findEvents(String source,
                                               String eventType,
                                               String routingKey,
                                               String correlationId,
                                               Instant from,
                                               Instant to,
                                               int page,
                                               int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "consumedAt"));

        Specification<AuditEventEntity> specification = (root, query, cb) -> cb.conjunction();
        if (source != null && !source.isBlank()) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("eventSource"), source));
        }
        if (eventType != null && !eventType.isBlank()) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("eventType"), eventType));
        }
        if (routingKey != null && !routingKey.isBlank()) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("routingKey"), routingKey));
        }
        if (correlationId != null && !correlationId.isBlank()) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("correlationId"), correlationId));
        }
        if (from != null) {
            specification = specification.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("consumedAt"), from));
        }
        if (to != null) {
            specification = specification.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("consumedAt"), to));
        }

        return repository.findAll(specification, pageable).map(this::toResponse);
    }

    @Override
    public Optional<AuditEventResponse> findById(Long auditId) {
        return repository.findById(auditId).map(this::toResponse);
    }

    private AuditEventResponse toResponse(AuditEventEntity entity) {
        return new AuditEventResponse(
                entity.getAuditId(),
                entity.getEventSource(),
                entity.getEventType(),
                entity.getRoutingKey(),
                entity.getExchangeName(),
                entity.getQueueName(),
                entity.getCorrelationId(),
                entity.getPayload(),
                entity.getOccurredAt(),
                entity.getConsumedAt()
        );
    }
}
