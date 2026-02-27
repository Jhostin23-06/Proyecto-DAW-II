package com.core.msauditoria.domain.exception;

public class AuditEventNotFoundException extends RuntimeException {
    public AuditEventNotFoundException(String message) {
        super(message);
    }
}
