package com.core.msusers.application.port.usecase;

import com.core.msusers.domain.bean.UserRequest;
import com.core.msusers.domain.bean.UserResponse;

public interface UpdateUserPort {
    UserResponse updateUser(String id, UserRequest request);
    void deactivateUser(String id);
}
