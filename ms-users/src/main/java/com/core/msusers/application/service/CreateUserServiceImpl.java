package com.core.msusers.application.service;

import com.core.msusers.application.port.outservice.UserPersistencePort;
import com.core.msusers.application.port.outservice.UserEventPort;
import com.core.msusers.application.port.usecase.CreateUserPort;
import com.core.msusers.domain.bean.UserRequest;
import com.core.msusers.domain.bean.UserResponse;
import com.core.msusers.domain.model.UserModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateUserServiceImpl implements CreateUserPort {

    private final UserPersistencePort userPersistencePort;
    private final UserEventPort userEventPort;
    private final UserModel userModel;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createUser(UserRequest request) {
        log.info("Creando nuevo usuario con email: {}", request.getUserEmail());

        // 1. Validar datos
        userModel.validateForCreation(request);

        // 2. Verificar email único
        if (userPersistencePort.existsByEmail(request.getUserEmail())) {
            throw new IllegalArgumentException("El email " + request.getUserEmail() + " ya está registrado");
        }

        // 3. Encriptar contraseña
        request.setUserPassword(passwordEncoder.encode(request.getUserPassword()));

        // 4. Guardar
        UserResponse response = userPersistencePort.save(request);
        log.info("Usuario creado exitosamente con ID: {}", response.getUserId());
        userEventPort.publishUserCreated(response);

        return response;
    }
}
