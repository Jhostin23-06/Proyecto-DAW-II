package core.cibertec.ms_servicios.infrastructure.controller;

import core.cibertec.ms_servicios.application.port.usecase.CreateShipmentPort;
import core.cibertec.ms_servicios.application.port.usecase.GetShipmentPort;
import core.cibertec.ms_servicios.domain.bean.ShipmentRequest;
import jakarta.validation.Valid;
import core.cibertec.ms_servicios.domain.bean.ShipmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final CreateShipmentPort createShipmentPort;
    private final GetShipmentPort getShipmentPort;

    @PostMapping
    public ResponseEntity<ShipmentResponse> createShipment(@Valid @RequestBody ShipmentRequest request) {
        ShipmentResponse resp = createShipmentPort.createShipment(request);
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipmentResponse> getById(@PathVariable Long id) {
        ShipmentResponse resp = getShipmentPort.getShipmentById(id);
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public ResponseEntity<List<ShipmentResponse>> getAll() {
        return ResponseEntity.ok(getShipmentPort.getAllShipments());
    }

}
