package com.core.msusers.application.port.outservice;

import com.core.msusers.domain.bean.UserRequest;
import com.core.msusers.domain.bean.UserResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface UserPersistencePort {
    UserResponse save(UserRequest request);
    Optional<UserResponse> findById(String id);
    UserResponse findByEmail(String email);
    List<UserResponse> findAll();
    List<UserResponse> findByRole(String role);
    UserResponse update(String id, UserRequest request);
    void deactivate(String id);
    boolean existsByEmail(String email);
    boolean existsById(String id);
    Optional<UserResponse> findByEmailAndPassword(String email, String password); // para autenticación (luego la reemplazaremos con JWT)
}
