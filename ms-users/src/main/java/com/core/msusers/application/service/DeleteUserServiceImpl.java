package com.core.msusers.application.service;

import com.core.msusers.application.port.outservice.UserPersistencePort;
import com.core.msusers.application.port.usecase.DeleteUserPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteUserServiceImpl implements DeleteUserPort {

    private final UserPersistencePort userPersistencePort;

    @Override
    public void deleteUser(String id) {
        log.info("Eliminando usuario con ID: {}", id);
        if (!userPersistencePort.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        // Si es borrado físico, implementa en el puerto y adaptador
        userPersistencePort.deactivate(id); // por ahora usamos desactivación
        log.info("Usuario eliminado (desactivado): {}", id);
    }
}
