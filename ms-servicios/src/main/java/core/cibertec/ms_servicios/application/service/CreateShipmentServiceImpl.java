package core.cibertec.ms_servicios.application.service;

import core.cibertec.ms_servicios.application.port.outservice.ShipmentEventPort;
import core.cibertec.ms_servicios.application.port.outservice.ShipmentPersistencePort;
import core.cibertec.ms_servicios.application.port.usecase.CreateShipmentPort;
import core.cibertec.ms_servicios.domain.bean.ShipmentRequest;
import core.cibertec.ms_servicios.domain.bean.ShipmentResponse;
import core.cibertec.ms_servicios.domain.model.ShipmentModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateShipmentServiceImpl implements CreateShipmentPort {

    private final ShipmentPersistencePort shipmentPersistencePort;
    private final ShipmentEventPort shipmentEventPort;
    private final ShipmentModel shipmentModel;

    private static final String CB_NAME = "shipmentService";

    @Override
    @CircuitBreaker(name = CB_NAME, fallbackMethod = "fallbackCreate")
    public ShipmentResponse createShipment(ShipmentRequest request) {
        log.info("Creating shipment, orderNumber={}", request.getOrderNumber());

        // Business validation using domain model
        shipmentModel.validateForCreation(request);

        ShipmentResponse saved = shipmentPersistencePort.save(request);

        try {
            shipmentEventPort.publishShipmentCreated(saved);
        } catch (Exception e) {
            log.error("Failed to publish shipment created event", e);
        }

        return saved;
    }

    public ShipmentResponse fallbackCreate(ShipmentRequest request, Throwable t) {
        log.error("Circuit breaker triggered for createShipment: {}", t.toString());
        ShipmentResponse resp = new ShipmentResponse();
        resp.setOrderNumber(request.getOrderNumber());
        resp.setDescription("PENDING - fallback response");
        return resp;
    }

}
