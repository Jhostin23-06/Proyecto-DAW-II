package com.core.msusers.infrastructure.controller;

import com.core.msusers.application.port.usecase.AuthenticateUserPort;
import com.core.msusers.domain.bean.LoginRequest;
import com.core.msusers.domain.bean.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticateUserPort authenticateUserPort;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authenticateUserPort.authenticate(request);
        return ResponseEntity.ok(response);
    }

}
