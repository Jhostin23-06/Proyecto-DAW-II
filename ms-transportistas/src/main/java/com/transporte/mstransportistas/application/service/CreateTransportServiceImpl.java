package com.transporte.mstransportistas.application.service;

import com.transporte.mstransportistas.application.port.outservice.TransportEventPort;
import com.transporte.mstransportistas.application.port.outservice.TransportPersistencePort;
import com.transporte.mstransportistas.application.port.outservice.UserServicePort;
import com.transporte.mstransportistas.application.port.usecase.CreateTransportPort;
import com.transporte.mstransportistas.domain.bean.TransportRequest;
import com.transporte.mstransportistas.domain.bean.TransportResponse;
import com.transporte.mstransportistas.domain.bean.UserInfo;
import com.transporte.mstransportistas.domain.model.TransportModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateTransportServiceImpl implements CreateTransportPort {

    private final TransportPersistencePort transportPersistencePort;
    private final TransportEventPort transportEventPort;
    private final UserServicePort userServicePort;
    private final TransportModel transportModel;

    @Override
    public TransportResponse createTransport(TransportRequest request) {

        // Validar que el usuario exista y sea transportista
//        UserInfo user = userServicePort.getUserById(request.getTransportUserId());
//        if (user == null) {
//            throw new IllegalArgumentException("El usuario con ID " + request.getTransportUserId() + " no existe");
//        }

//        if (!"TRANSPORTER".equals(user.getUserRole())) {
//            throw new IllegalArgumentException("El usuario no tiene rol de transportista");
//        }
//        if (!user.isActive()) {
//            throw new IllegalArgumentException("El usuario no está activo");
//        }

        log.info("Creando nuevo transporte con placa: {}", request.getTransportLicensePlate());

        transportModel.validateForCreation(request);

        if (transportPersistencePort.existsByLicensePlate(request.getTransportLicensePlate())) {
            throw new IllegalArgumentException("La placa " + request.getTransportLicensePlate() + " ya existe");
        }

        TransportResponse response = transportPersistencePort.save(request);

        try {
            transportEventPort.publishTransportCreated(response);
        } catch (Exception e) {
            log.error("No se pudo publicar el evento de creación", e);
            // No interrumpimos el flujo principal
        }

        return response;

    }
}
