package com.core.msnotificaciones.infrastructure.outservice.dto;

public record TransportSummaryResponse(
        String transportId,
        String transportUserId,
        String transportStatus,
        Boolean available
) {
}
