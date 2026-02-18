package com.core.msusers.application.port.usecase;

import com.core.msusers.domain.bean.UserRequest;
import com.core.msusers.domain.bean.UserResponse;

public interface UpdateUserPort {
    UserResponse updateUser(Long id, UserRequest request);
    void deactivateUser(Long id);
}
