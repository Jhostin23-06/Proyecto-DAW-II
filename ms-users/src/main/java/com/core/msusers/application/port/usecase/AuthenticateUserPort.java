package com.core.msusers.application.port.usecase;

import com.core.msusers.domain.bean.LoginRequest;
import com.core.msusers.domain.bean.LoginResponse;

public interface AuthenticateUserPort {
    LoginResponse authenticate(LoginRequest request);
}
