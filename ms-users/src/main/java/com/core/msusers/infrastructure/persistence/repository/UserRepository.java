package com.core.msusers.infrastructure.persistence.repository;

import com.core.msusers.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findByUserEmail(String email);

    Optional<UserEntity> findById(String id);

    boolean existsById(String id);

    List<UserEntity> findByUserRoleAndActiveTrue(String role);

    List<UserEntity> findByActiveTrue();

    boolean existsByUserEmail(String email);

}
