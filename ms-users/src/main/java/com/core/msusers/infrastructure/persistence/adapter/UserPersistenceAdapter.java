package com.core.msusers.infrastructure.persistence.adapter;

import com.core.msusers.application.port.outservice.UserPersistencePort;
import com.core.msusers.domain.bean.UserRequest;
import com.core.msusers.domain.bean.UserResponse;
import com.core.msusers.infrastructure.persistence.entity.UserEntity;
import com.core.msusers.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPersistencePort {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserResponse save(UserRequest request) {
        UserEntity entity = modelMapper.map(request, UserEntity.class);
        entity.setActive(true);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        UserEntity saved = userRepository.save(entity);
        return toResponse(saved);
    }

    @Override
    public Optional<UserResponse> findById(Long id) {
        return userRepository.findById(id)
                .map(this::toResponse);
    }

    @Override
    public Optional<UserResponse> findByEmail(String email) {
        return userRepository.findByUserEmail(email)
                .map(this::toResponse);
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findByActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> findByRole(String role) {
        return userRepository.findByUserRole(role).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse update(Long id, UserRequest request) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        if (request.getUserName() != null) {
            entity.setUserName(request.getUserName());
        }
        if (request.getUserEmail() != null) {
            entity.setUserEmail(request.getUserEmail());
        }
        if (request.getUserPassword() != null) {
            entity.setUserPassword(request.getUserPassword());
        }
        if (request.getUserRole() != null) {
            entity.setUserRole(request.getUserRole());
        }

        entity.setUpdatedAt(LocalDateTime.now());
        UserEntity updated = userRepository.save(entity);
        return toResponse(updated);
    }

    @Override
    public void deactivate(Long id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        entity.setActive(false);
        entity.setUpdatedAt(LocalDateTime.now());
        userRepository.save(entity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByUserEmail(email);
    }

    @Override
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    public Optional<UserResponse> findByEmailAndPassword(String email, String password) {
        return Optional.empty();
    }

    private UserResponse toResponse(UserEntity entity) {
        UserResponse response = modelMapper.map(entity, UserResponse.class);
        return response;
    }
}
