package com.core.msnotificaciones.application.service;

import com.core.msnotificaciones.domain.bean.NotificationResponse;
import com.core.msnotificaciones.domain.exception.NotificationNotFoundException;
import com.core.msnotificaciones.infrastructure.persistence.entity.NotificationEntity;
import com.core.msnotificaciones.infrastructure.persistence.repository.NotificationRepository;
import com.core.msnotificaciones.infrastructure.stream.NotificationSseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationSseService notificationSseService;

    @Transactional
    public NotificationResponse recordEvent(NotificationEventData eventData) {
        NotificationEntity entity = new NotificationEntity();
        entity.setEventSource(eventData.eventSource());
        entity.setEventType(eventData.eventType());
        entity.setRoutingKey(eventData.routingKey());
        entity.setExchangeName(eventData.exchangeName());
        entity.setQueueName(eventData.queueName());
        entity.setTitle(eventData.title());
        entity.setMessage(eventData.message());
        entity.setPayload(eventData.payload());
        entity.setCorrelationId(eventData.correlationId());
        entity.setTargetUserId(eventData.targetUserId());
        entity.setOccurredAt(eventData.occurredAt() == null ? Instant.now() : eventData.occurredAt());
        entity.setReceivedAt(Instant.now());
        entity.setRead(Boolean.FALSE);

        NotificationEntity saved = notificationRepository.save(entity);
        NotificationResponse response = toResponse(saved);
        try {
            notificationSseService.broadcast(response);
        } catch (Exception ex) {
            // El push SSE no debe afectar persistencia.
            log.warn("No se pudo enviar notificacion por SSE notificationId={}: {}",
                    response.notificationId(), ex.toString());
        }
        return response;
    }

    public Page<NotificationResponse> findNotifications(String source, Boolean read, String transportUserId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "receivedAt"));
        Specification<NotificationEntity> specification = (root, query, cb) -> cb.isNotNull(root.get("targetUserId"));

        if (source != null && !source.isBlank()) {
            specification = specification.and((root, query, cb) ->
                    cb.equal(cb.lower(root.get("eventSource")), source.trim().toLowerCase()));
        }
        if (read != null) {
            specification = specification.and((root, query, cb) ->
                    cb.equal(root.get("read"), read));
        }
        if (transportUserId != null && !transportUserId.isBlank()) {
            specification = specification.and((root, query, cb) ->
                    cb.equal(root.get("targetUserId"), transportUserId.trim()));
        }

        return notificationRepository.findAll(specification, pageable).map(this::toResponse);
    }

    public NotificationResponse findById(Long notificationId, String transportUserId) {
        return notificationRepository.findById(notificationId)
                .filter(entity -> canAccess(entity, transportUserId))
                .map(this::toResponse)
                .orElseThrow(() -> new NotificationNotFoundException(
                        "Notificacion no encontrada: " + notificationId
                ));
    }

    @Transactional
    public NotificationResponse markAsRead(Long notificationId, String transportUserId) {
        NotificationEntity entity = notificationRepository.findById(notificationId)
                .filter(found -> canAccess(found, transportUserId))
                .orElseThrow(() -> new NotificationNotFoundException(
                        "Notificacion no encontrada: " + notificationId
                ));

        if (!Boolean.TRUE.equals(entity.getRead())) {
            entity.setRead(Boolean.TRUE);
            entity.setReadAt(Instant.now());
            entity = notificationRepository.save(entity);
        }
        return toResponse(entity);
    }

    @Transactional
    public int markAllAsRead(String transportUserId) {
        if (transportUserId != null && !transportUserId.isBlank()) {
            return notificationRepository.markAllAsReadByTargetUserId(transportUserId.trim(), Instant.now());
        }
        return notificationRepository.markAllTargetedAsRead(Instant.now());
    }

    public long countUnread(String transportUserId) {
        if (transportUserId != null && !transportUserId.isBlank()) {
            return notificationRepository.countByReadFalseAndTargetUserId(transportUserId.trim());
        }
        return notificationRepository.countByReadFalseAndTargetUserIdIsNotNull();
    }

    private NotificationResponse toResponse(NotificationEntity entity) {
        return new NotificationResponse(
                entity.getNotificationId(),
                entity.getEventSource(),
                entity.getEventType(),
                entity.getTitle(),
                entity.getMessage(),
                entity.getPayload(),
                entity.getCorrelationId(),
                entity.getTargetUserId(),
                entity.getRead(),
                entity.getOccurredAt(),
                entity.getReceivedAt(),
                entity.getReadAt()
        );
    }

    private boolean canAccess(NotificationEntity entity, String transportUserId) {
        if (transportUserId == null || transportUserId.isBlank()) {
            return true;
        }
        if (entity.getTargetUserId() == null || entity.getTargetUserId().isBlank()) {
            return false;
        }
        return entity.getTargetUserId().equalsIgnoreCase(transportUserId.trim());
    }

    public record NotificationEventData(
            String eventSource,
            String eventType,
            String routingKey,
            String exchangeName,
            String queueName,
            String title,
            String message,
            String payload,
            String correlationId,
            String targetUserId,
            Instant occurredAt
    ) {}
}
