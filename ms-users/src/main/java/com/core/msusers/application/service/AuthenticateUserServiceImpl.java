package com.core.msusers.application.service;

import com.core.msusers.application.port.outservice.UserPersistencePort;
import com.core.msusers.application.port.usecase.AuthenticateUserPort;
import com.core.msusers.domain.bean.LoginRequest;
import com.core.msusers.domain.bean.LoginResponse;
import com.core.msusers.domain.bean.UserResponse;
import com.core.msusers.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticateUserServiceImpl implements AuthenticateUserPort {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserPersistencePort userPersistencePort;

    @Override
    public LoginResponse authenticate(LoginRequest request) {
        log.info("Autenticando usuario con email: {}", request.getUserEmail());

        // 1. Autenticar con Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUserEmail(),
                        request.getUserPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. Generar token JWT
        String jwt = tokenProvider.generateToken(authentication);

        // 3. Obtener datos del usuario
        UserResponse user = userPersistencePort.findByEmail(request.getUserEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 4. Construir respuesta
        return new LoginResponse(
                jwt,
                "Bearer",
                user.getUserId(),
                user.getUserName(),
                user.getUserEmail(),
                user.getUserRole()
        );
    }
}
