package com.core.msusers.application.port.usecase;

import com.core.msusers.domain.bean.UserResponse;

import java.util.List;

public interface GetUserPort {
    UserResponse getUserById(String id);
    UserResponse getUserByEmail(String email);
    List<UserResponse> getAllUsers();
    List<UserResponse> getUsersByRole(String role);
}
