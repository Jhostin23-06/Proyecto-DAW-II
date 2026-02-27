package core.cibertec.ms_servicios.infrastructure.controller;

import core.cibertec.ms_servicios.application.port.usecase.CreateShipmentPort;
import core.cibertec.ms_servicios.application.port.usecase.DeleteShipmentPort;
import core.cibertec.ms_servicios.application.port.usecase.GetShipmentPort;
import core.cibertec.ms_servicios.application.port.usecase.UpdateShipmentStatusPort;
import core.cibertec.ms_servicios.domain.bean.ShipmentRequest;
import core.cibertec.ms_servicios.domain.bean.UpdateShipmentStatusRequest;
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
    private final UpdateShipmentStatusPort updateShipmentStatusPort;
    private final DeleteShipmentPort deleteShipmentPort;

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
    public ResponseEntity<List<ShipmentResponse>> getAll(
            @RequestParam(required = false) String transportId,
            @RequestParam(required = false) String userId) {

        if (userId != null && !userId.isBlank()) {
            List<ShipmentResponse> byUser = getShipmentPort.getShipmentsByTransportUserId(userId);
            if (transportId != null && !transportId.isBlank()) {
                byUser = byUser.stream()
                        .filter(s -> transportId.equals(s.getTransportId()))
                        .toList();
            }
            return ResponseEntity.ok(byUser);
        }

        if (transportId != null && !transportId.isBlank()) {
            return ResponseEntity.ok(getShipmentPort.getShipmentsByTransportId(transportId));
        }

        return ResponseEntity.ok(getShipmentPort.getAllShipments());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ShipmentResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateShipmentStatusRequest request) {

        ShipmentResponse resp = updateShipmentStatusPort.updateShipmentStatus(id, request.getStatusId());
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShipment(@PathVariable Long id) {
        boolean deleted = deleteShipmentPort.deleteShipment(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

}
