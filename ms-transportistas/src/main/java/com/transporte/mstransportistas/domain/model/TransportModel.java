package com.transporte.mstransportistas.domain.model;

import com.transporte.mstransportistas.domain.bean.TransportRequest;
import com.transporte.mstransportistas.domain.constraints.StatusTransitionValidation;
import com.transporte.mstransportistas.domain.constraints.TransportValidation;
import org.springframework.stereotype.Component;

@Component
public class TransportModel implements TransportValidation, StatusTransitionValidation {
    @Override
    public boolean validateTransition(TransportStatus currentStatus, TransportStatus newStatus) {
        if (currentStatus == newStatus) {
            return true; // mismo estado, siempre válido
        }

        switch (currentStatus) {
            case AVAILABLE:
                // Desde disponible puede ir a cualquier estado excepto a sí mismo (ya cubierto)
                return true; // Permitir todos: IN_TRANSIT, MAINTENANCE, OUT_OF_SERVICE
            case IN_TRANSIT:
                // En tránsito solo puede volver a disponible o ir a mantenimiento
                return newStatus == TransportStatus.AVAILABLE || newStatus == TransportStatus.MAINTENANCE;
            case MAINTENANCE:
                // En mantenimiento solo puede volver a disponible (o quizás a out of service)
                return newStatus == TransportStatus.AVAILABLE || newStatus == TransportStatus.OUT_OF_SERVICE;
            case OUT_OF_SERVICE:
                // Fuera de servicio no puede cambiar a otro estado (solo se puede reactivar? Depende de la lógica)
                // Por ahora, no permitir cambios desde OUT_OF_SERVICE
                return false;
            default:
                return false;
        }
    }

    @Override
    public String getTransitionMessage(TransportStatus currentStatus, TransportStatus newStatus) {
        if (currentStatus == newStatus) {
            return "El estado ya es " + currentStatus;
        }
        switch (currentStatus) {
            case AVAILABLE:
                return "Disponible puede cambiar a cualquier estado";
            case IN_TRANSIT:
                return "En tránsito solo puede cambiar a Disponible o Mantenimiento";
            case MAINTENANCE:
                return "En mantenimiento solo puede cambiar a Disponible o Fuera de servicio";
            case OUT_OF_SERVICE:
                return "Fuera de servicio no puede cambiar de estado";
            default:
                return "Transición no válida";
        }
    }

    @Override
    public boolean validateForCreation(TransportRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request no puede ser nulo");
        }
//        if (request.getTransportUserId() == null) {
//            throw new IllegalArgumentException("transportUserId es requerido.");
//        }
        if (request.getTransportType() == null || request.getTransportType().trim().isEmpty()) {
            throw new IllegalArgumentException("transportType es requerido");
        }
        if (!validateCapacity(request.getTransportCapacity())) {
            throw new IllegalArgumentException("Capacidad debe ser mayor que 0");
        }
        if (request.getTransportDriver() == null || request.getTransportDriver().trim().isEmpty()) {
            throw new IllegalArgumentException("Chofer es requerido");
        }
        if (!validateLicensePlate(request.getTransportLicensePlate())) {
            throw new IllegalArgumentException("Placa inválida");
        }
        if (request.getTransportCompany() == null || request.getTransportCompany().trim().isEmpty()) {
            throw new IllegalArgumentException("Compañía es requerida");
        }
        return true;
    }

    @Override
    public boolean validateForUpdate(TransportRequest request) {
        // Para actualización, solo validamos los campos que vienen (no todos son obligatorios)
        if (request == null) {
            throw new IllegalArgumentException("Request no puede ser nulo");
        }
        if (request.getTransportUserId() != null) {
            throw new IllegalArgumentException("transportUserId no se puede actualizar");
        }
        if (request.getTransportCapacity() != null && !validateCapacity(request.getTransportCapacity())) {
            throw new IllegalArgumentException("Capacidad inválida");
        }
        if (request.getTransportLicensePlate() != null && !validateLicensePlate(request.getTransportLicensePlate())) {
            throw new IllegalArgumentException("Placa inválida");
        }
        return true;
    }

    @Override
    public boolean validateLicensePlate(String licensePlate) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            return false;
        }
        // Formato: ABC-123 o ABC1234
        return licensePlate.matches("^[A-Z]{3}-?\\d{3,4}$");
    }

    @Override
    public boolean validateCapacity(Double capacity) {
        return capacity != null && capacity > 0 && capacity <= 100000;
    }
}
