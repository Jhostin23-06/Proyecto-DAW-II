package com.core.msnotificaciones.infrastructure.controller;

import java.time.Instant;

public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        String correlationId,
        Instant timestamp
) {
}
