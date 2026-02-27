package com.core.msauditoria.infrastructure.controller;

import com.core.msauditoria.application.port.usecase.GetAuditEventsPort;
import com.core.msauditoria.domain.bean.AuditEventResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@RestController
@RequestMapping("/api/audit/events")
@RequiredArgsConstructor
public class AuditController {

    private final GetAuditEventsPort getAuditEventsPort;

    @GetMapping
    public ResponseEntity<Page<AuditEventResponse>> findEvents(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String routingKey,
            @RequestParam(required = false) String correlationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(getAuditEventsPort.findEvents(
                source, eventType, routingKey, correlationId, from, to, page, size
        ));
    }

    @GetMapping("/{auditId}")
    public ResponseEntity<AuditEventResponse> findById(@PathVariable Long auditId) {
        return ResponseEntity.ok(getAuditEventsPort.findById(auditId));
    }
}
