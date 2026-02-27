package com.core.msnotificaciones.infrastructure.stream;

import com.core.msnotificaciones.domain.bean.NotificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class NotificationSseService {

    private static final long SSE_TIMEOUT_MS = 0L;
    private final List<Subscription> subscriptions = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe(String sourceFilter, String transportUserId) {
        String normalizedFilter = normalizeSource(sourceFilter);
        String normalizedTransportUserId = normalizeValue(transportUserId);
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
        Subscription subscription = new Subscription(emitter, normalizedFilter, normalizedTransportUserId);
        subscriptions.add(subscription);

        emitter.onCompletion(() -> subscriptions.remove(subscription));
        emitter.onTimeout(() -> {
            subscriptions.remove(subscription);
            emitter.complete();
        });
        emitter.onError(ex -> subscriptions.remove(subscription));

        sendConnectedEvent(subscription);
        return emitter;
    }

    public void broadcast(NotificationResponse notification) {
        if (subscriptions.isEmpty()) {
            return;
        }

        for (Subscription subscription : subscriptions) {
            if (!matchesFilter(subscription.sourceFilter(), notification.eventSource())) {
                continue;
            }
            if (!matchesFilter(subscription.transportUserId(), notification.targetUserId())) {
                continue;
            }
            try {
                subscription.emitter().send(SseEmitter.event()
                        .id(String.valueOf(notification.notificationId()))
                        .name("notification")
                        .data(notification));
            } catch (Exception ex) {
                subscriptions.remove(subscription);
                try {
                    subscription.emitter().complete();
                } catch (Exception completeEx) {
                    log.debug("Error cerrando suscripcion SSE tras fallo de envio: {}", completeEx.getMessage());
                }
                log.debug("Suscripcion SSE removida por error de envio: {}", ex.getMessage());
            }
        }
    }

    private void sendConnectedEvent(Subscription subscription) {
        String source = subscription.sourceFilter() == null ? "all" : subscription.sourceFilter();
        String targetUserId = subscription.transportUserId() == null ? "all" : subscription.transportUserId();
        try {
            subscription.emitter().send(SseEmitter.event()
                    .name("connected")
                    .data(Map.of("status", "connected", "source", source, "transportUserId", targetUserId)));
        } catch (IOException ex) {
            subscriptions.remove(subscription);
            subscription.emitter().completeWithError(ex);
        }
    }

    private boolean matchesFilter(String sourceFilter, String eventSource) {
        if (sourceFilter == null) {
            return true;
        }
        if (eventSource == null) {
            return false;
        }
        return sourceFilter.equalsIgnoreCase(eventSource);
    }

    private String normalizeSource(String source) {
        return normalizeValue(source);
    }

    private String normalizeValue(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private record Subscription(SseEmitter emitter, String sourceFilter, String transportUserId) {
    }
}
