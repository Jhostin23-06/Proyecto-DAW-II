package core.cibertec.ms_servicios.application.service;

import core.cibertec.ms_servicios.application.port.outservice.ClientValidationPort;
import core.cibertec.ms_servicios.application.port.outservice.ShipmentEventPort;
import core.cibertec.ms_servicios.application.port.outservice.ShipmentPersistencePort;
import core.cibertec.ms_servicios.application.port.outservice.TransportValidationPort;
import core.cibertec.ms_servicios.application.port.usecase.CreateShipmentPort;
import core.cibertec.ms_servicios.domain.bean.ShipmentRequest;
import core.cibertec.ms_servicios.domain.bean.ShipmentResponse;
import core.cibertec.ms_servicios.domain.exception.ExternalServiceException;
import core.cibertec.ms_servicios.domain.model.ShipmentModel;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateShipmentServiceImpl implements CreateShipmentPort {

    private final ShipmentPersistencePort shipmentPersistencePort;
    private final ShipmentEventPort shipmentEventPort;
    private final ShipmentModel shipmentModel;
    private final ClientValidationPort clientValidationPort;
    private final TransportValidationPort transportValidationPort;

    private static final String CB_NAME = "shipmentService";
    private static final String RETRY_NAME = "shipmentServiceRetry";

    @Override
    @Retry(name = RETRY_NAME, fallbackMethod = "fallbackCreate")
    @CircuitBreaker(name = CB_NAME, fallbackMethod = "fallbackCreate")
    public ShipmentResponse createShipment(ShipmentRequest request) {
        log.info("Creating shipment, orderNumber={}", request.getOrderNumber());

        shipmentModel.validateForCreation(request);

        if (!clientValidationPort.existsById(request.getClientId())) {
            throw new ValidationException("Cliente no existe.");
        }

        Optional<String> statusOpt = transportValidationPort.getStatusById(request.getTransportId());
        log.info("Transport validation for createShipment transportId={}, statusPresent={}",
                request.getTransportId(), statusOpt.isPresent());

        if (statusOpt.isEmpty()) {
            throw new ValidationException("Transporte no existe.");
        }

        String status = statusOpt.get().trim().toUpperCase();
        log.info("Transport status resolved for createShipment transportId={} status={}",
                request.getTransportId(), status);

        if (!"AVAILABLE".equals(status)) {
            throw new ValidationException("El estado de este transporte esta " + status + " y no puede ser usado para este envio");
        }

        ShipmentResponse saved = shipmentPersistencePort.save(request);

        try {
            shipmentEventPort.publishShipmentCreated(saved);
        } catch (Exception e) {
            log.error("Failed to publish shipment created event", e);
        }

        return saved;
    }

    public ShipmentResponse fallbackCreate(ShipmentRequest request, Throwable t) {
        if (t instanceof ValidationException || t instanceof IllegalArgumentException) {
            if (t instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new RuntimeException(t);
        }

        log.error("Circuit breaker triggered for createShipment: {}", t.toString());
        throw new ExternalServiceException("No se pudo completar la creacion del envio por error de integracion", t);
    }
}
