package com.transporte.mstransportistas.infrastructure.controller;

import com.transporte.mstransportistas.application.port.usecase.CreateTransportPort;
import com.transporte.mstransportistas.application.port.usecase.DeleteTransportPort;
import com.transporte.mstransportistas.application.port.usecase.FindAvailableTransportPort;
import com.transporte.mstransportistas.application.port.usecase.GetTransportPort;
import com.transporte.mstransportistas.application.port.usecase.UpdateStatusPort;
import com.transporte.mstransportistas.application.port.usecase.UpdateTransportPort;
import com.transporte.mstransportistas.domain.bean.AssignRequest;
import com.transporte.mstransportistas.domain.bean.TransportRequest;
import com.transporte.mstransportistas.domain.bean.TransportResponse;
import com.transporte.mstransportistas.domain.bean.UpdateStatusRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transports")
@RequiredArgsConstructor
@Validated
public class TransportController {

    private final CreateTransportPort createTransportPort;
    private final GetTransportPort getTransportPort;
    private final UpdateStatusPort updateStatusPort;
    private final UpdateTransportPort updateTransportPort;
    private final DeleteTransportPort deleteTransportPort;
    private final FindAvailableTransportPort findAvailableTransportPort;

    @PostMapping
    public ResponseEntity<TransportResponse> createTransport(
            @Valid @RequestBody TransportRequest request) {
        TransportResponse response = createTransportPort.createTransport(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransportResponse> getTransportById(@PathVariable String id) {
        TransportResponse response = getTransportPort.getTransportById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TransportResponse>> getAllTransports(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String userId) {

        List<TransportResponse> responses;
        if (status != null) {
            responses = getTransportPort.getTransportsByStatus(status);
        } else if (userId != null) {
            responses = getTransportPort.getTransportsByUserId(userId);
        } else {
            responses = getTransportPort.getAllTransports();
        }

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/available/search")
    public ResponseEntity<List<TransportResponse>> findAvailableTransports(
            @RequestParam @NotNull @Positive Double requiredCapacity,
            @RequestParam(required = false) String location) {

        List<TransportResponse> responses = findAvailableTransportPort
                .findAvailableTransports(requiredCapacity, location);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TransportResponse> updateTransportStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateStatusRequest request) {

        TransportResponse response = updateStatusPort.updateTransportStatus(
                id,
                request.getTransportStatus(),
                request.getLocation(),
                request.getReason()
        );
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/location")
    public ResponseEntity<TransportResponse> updateLocation(
            @PathVariable String id,
            @RequestParam String location) {

        TransportResponse response = updateStatusPort.updateTransportStatus(
                id, null, location, "Actualizacion de ubicacion"
        );
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TransportResponse> updateTransport(
            @PathVariable String id,
            @Valid @RequestBody TransportRequest request) {

        TransportResponse response = updateTransportPort.updateTransport(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<TransportResponse> assignTransport(
            @PathVariable String id,
            @Valid @RequestBody AssignRequest request) {
        TransportResponse response = updateTransportPort.assignToUser(id, request.getUserId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransport(@PathVariable String id) {
        deleteTransportPort.deleteTransport(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Transport Service is running");
    }
}
