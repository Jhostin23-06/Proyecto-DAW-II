package com.core.msusers.infrastructure.persistence.repository;

import com.core.msusers.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUserEmail(String email);

    List<UserEntity> findByUserRole(String role);

    List<UserEntity> findByActiveTrue();

    boolean existsByUserEmail(String email);

}
