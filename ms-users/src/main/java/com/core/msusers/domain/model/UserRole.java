package com.core.msusers.domain.model;

public enum UserRole {
    CLIENT("Cliente"),
    TRANSPORTER("Transportista"),
    OPERATOR("Operador"),
    ADMIN("Administrador");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
