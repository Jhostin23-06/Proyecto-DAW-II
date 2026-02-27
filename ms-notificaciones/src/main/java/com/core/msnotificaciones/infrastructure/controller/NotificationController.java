package com.core.msnotificaciones.infrastructure.controller;

import com.core.msnotificaciones.application.service.NotificationService;
import com.core.msnotificaciones.domain.bean.NotificationResponse;
import com.core.msnotificaciones.infrastructure.stream.NotificationSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationSseService notificationSseService;

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> findNotifications(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) Boolean read,
            @RequestParam String transportUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(notificationService.findNotifications(source, read, transportUserId, page, size));
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResponse> findById(
            @PathVariable Long notificationId,
            @RequestParam String transportUserId) {
        return ResponseEntity.ok(notificationService.findById(notificationId, transportUserId));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable Long notificationId,
            @RequestParam String transportUserId) {
        return ResponseEntity.ok(notificationService.markAsRead(notificationId, transportUserId));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Map<String, Integer>> markAllAsRead(@RequestParam String transportUserId) {
        int updated = notificationService.markAllAsRead(transportUserId);
        return ResponseEntity.ok(Map.of("updated", updated));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> unreadCount(@RequestParam String transportUserId) {
        return ResponseEntity.ok(Map.of("unreadCount", notificationService.countUnread(transportUserId)));
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(
            @RequestParam(required = false) String source,
            @RequestParam String transportUserId) {
        return notificationSseService.subscribe(source, transportUserId);
    }
}
