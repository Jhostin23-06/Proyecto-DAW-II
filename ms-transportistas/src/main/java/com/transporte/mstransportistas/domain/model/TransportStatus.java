package com.transporte.mstransportistas.domain.model;

public enum TransportStatus {
    AVAILABLE("Disponible", true),
    IN_TRANSIT("En tránsito", true),
    MAINTENANCE("En mantenimiento", false),
    OUT_OF_SERVICE("Fuera de servicio", false);

    private final String description;
    private final boolean operational;

    TransportStatus(String description, boolean operational) {
        this.description = description;
        this.operational = operational;
    }

    public String getDescription() {
        return description;
    }

    public boolean isOperational() {
        return operational;
    }

    public static TransportStatus fromString(String status) {
        for (TransportStatus ts : TransportStatus.values()) {
            if (ts.name().equalsIgnoreCase(status)) {
                return ts;
            }
        }
        throw new IllegalArgumentException("Estado no válido: " + status);
    }
}
