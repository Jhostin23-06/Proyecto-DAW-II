package com.core.msusers.application.service;

import com.core.msusers.application.port.outservice.UserPersistencePort;
import com.core.msusers.application.port.usecase.UpdateUserPort;
import com.core.msusers.domain.bean.UserRequest;
import com.core.msusers.domain.bean.UserResponse;
import com.core.msusers.domain.model.UserModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateUserServiceImpl implements UpdateUserPort {

    private final UserPersistencePort userPersistencePort;
    private final UserModel userModel;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse updateUser(String id, UserRequest request) {
        log.info("Actualizando usuario con ID: {}", id);

        // 1. Validar datos de actualización
        userModel.validateForUpdate(request);

        // 2. Verificar que el usuario existe
        UserResponse existing = userPersistencePort.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // 3. Si se actualiza email, verificar que no exista otro con ese email
        if (request.getUserEmail() != null && !request.getUserEmail().equals(existing.getUserEmail())) {
            if (userPersistencePort.existsByEmail(request.getUserEmail())) {
                throw new IllegalArgumentException("El email " + request.getUserEmail() + " ya está en uso");
            }
        }

        // 4. Si se actualiza contraseña, encriptarla
        if (request.getUserPassword() != null) {
            request.setUserPassword(passwordEncoder.encode(request.getUserPassword()));
        }

        // 5. Actualizar
        UserResponse updated = userPersistencePort.update(id, request);
        log.info("Usuario actualizado exitosamente: {}", id);

        return updated;
    }

    @Override
    public void deactivateUser(String id) {
        log.info("Desactivando usuario con ID: {}", id);
        if (!userPersistencePort.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        userPersistencePort.deactivate(id);
        log.info("Usuario desactivado: {}", id);
    }
}
