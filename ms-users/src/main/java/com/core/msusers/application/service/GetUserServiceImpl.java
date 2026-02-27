package com.core.msusers.application.service;

import com.core.msusers.application.port.outservice.UserPersistencePort;
import com.core.msusers.application.port.usecase.GetUserPort;
import com.core.msusers.domain.bean.UserResponse;
import com.core.msusers.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetUserServiceImpl implements GetUserPort {

    private final UserPersistencePort userPersistencePort;


    @Override
    public UserResponse getUserById(String id) {
        log.debug("Obteniendo usuario por ID: {}", id);
        return userPersistencePort.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + id));
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        log.debug("Obteniendo usuario por email: {}", email);
        return userPersistencePort.findByEmail(email);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        log.debug("Obteniendo todos los usuarios");
        return userPersistencePort.findAll();
    }

    @Override
    public List<UserResponse> getUsersByRole(String role) {
        log.debug("Obteniendo usuarios por rol: {}", role);
        return userPersistencePort.findByRole(role);
    }
}
