package core.cibertec.ms_servicios.domain.model;

import core.cibertec.ms_servicios.domain.bean.ShipmentRequest;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class ShipmentModel {

    public boolean validateForCreation(ShipmentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (request.getCategoryId() == null || request.getCategoryId() <= 0) {
            throw new ValidationException("categoryId is required and must be positive");
        }
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new ValidationException("description is required");
        }
        if (request.getPrice() == null || request.getPrice() <= 0) {
            throw new ValidationException("price is required and must be positive");
        }
        if (request.getWeight() == null || request.getWeight() <= 0) {
            throw new ValidationException("weight is required and must be positive");
        }
        if (request.getVolume() == null || request.getVolume() <= 0) {
            throw new ValidationException("volume is required and must be positive");
        }
        if (request.getOrigin() == null || request.getOrigin().trim().isEmpty()) {
            throw new ValidationException("origin is required");
        }
        if (request.getDestination() == null || request.getDestination().trim().isEmpty()) {
            throw new ValidationException("destination is required");
        }
        if (request.getClientId() == null || request.getClientId() <= 0) {
            throw new ValidationException("clientId is required and must be positive");
        }
        if (request.getTransportId() == null || request.getTransportId().trim().isEmpty()) {
            throw new ValidationException("transportId is required");
        }
        if (request.getOrderNumber() == null || request.getOrderNumber().trim().isEmpty()) {
            throw new ValidationException("orderNumber is required");
        }

        return true;
    }

    public boolean validateForUpdate(ShipmentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        // For update, only validate fields present
        if (request.getPrice() != null && request.getPrice() <= 0) {
            throw new ValidationException("price must be positive");
        }
        if (request.getWeight() != null && request.getWeight() <= 0) {
            throw new ValidationException("weight must be positive");
        }
        if (request.getVolume() != null && request.getVolume() <= 0) {
            throw new ValidationException("volume must be positive");
        }
        return true;
    }

}
