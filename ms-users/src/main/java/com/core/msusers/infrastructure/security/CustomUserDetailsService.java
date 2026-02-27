package com.core.msusers.infrastructure.security;

import com.core.msusers.application.port.outservice.UserPersistencePort;
import com.core.msusers.domain.bean.UserResponse;
import com.core.msusers.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserPersistencePort userPersistencePort;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserResponse user;
        try {
            user = userPersistencePort.findByEmail(email);
        } catch (UserNotFoundException ex) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + email, ex);
        }

        return new User(
                user.getUserEmail(),
                user.getUserPassword(), // debe estar encriptada
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getUserRole()))
        );
    }
}
