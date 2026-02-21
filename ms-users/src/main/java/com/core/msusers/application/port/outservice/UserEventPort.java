package com.core.msusers.application.port.outservice;

import com.core.msusers.domain.bean.UserResponse;

public interface UserEventPort {
    void publishUserCreated(UserResponse user);
    void publishUserUpdated(UserResponse user);
    void publishUserDeleted(String userId);
}
