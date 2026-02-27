package com.core.msusers.application.service;

import com.core.msusers.application.port.outservice.UserEventPort;
import com.core.msusers.application.port.outservice.UserPersistencePort;
import com.core.msusers.application.port.usecase.DeleteUserPort;
import com.core.msusers.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteUserServiceImpl implements DeleteUserPort {

    private final UserPersistencePort userPersistencePort;
    private final UserEventPort userEventPort;

    @Override
    public void deleteUser(String id) {
        log.info("Eliminando usuario con ID: {}", id);
        if (!userPersistencePort.existsById(id)) {
            throw new UserNotFoundException("Usuario no encontrado con ID: " + id);
        }
        userPersistencePort.deactivate(id);
        userEventPort.publishUserDeleted(id);
        log.info("Usuario eliminado (desactivado): {}", id);
    }
}
