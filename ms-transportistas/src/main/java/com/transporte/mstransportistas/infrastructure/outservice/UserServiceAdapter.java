package com.transporte.mstransportistas.infrastructure.outservice;

import com.transporte.mstransportistas.application.port.outservice.UserServicePort;
import com.transporte.mstransportistas.application.port.outservice.client.UserFeignClient;
import com.transporte.mstransportistas.domain.bean.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserServiceAdapter implements UserServicePort {

    private final UserFeignClient userFeignClient;

    @Override
    public UserInfo getUserById(String userId) {
        try {
            return userFeignClient.getUserById(userId);
        } catch (Exception e) {
            // Manejar error, podría ser que el usuario no exista o el servicio no responda
            return null;
        }
    }
}
