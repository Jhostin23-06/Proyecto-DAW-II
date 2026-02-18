package com.core.msusers.domain.model;

import com.core.msusers.domain.bean.UserRequest;
import com.core.msusers.domain.contraints.RoleValidation;
import com.core.msusers.domain.contraints.UserValidation;
import org.springframework.stereotype.Component;

@Component
public class UserModel implements UserValidation, RoleValidation {
    @Override
    public boolean isValidRole(String role) {
        return validateRole(role);
    }

    @Override
    public UserRole fromString(String role) {
        try {
            return UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rol inválido: " + role);
        }
    }

    @Override
    public boolean validateForCreation(UserRequest request) {
        if (request == null) throw new IllegalArgumentException("Request no puede ser nulo");
        if (request.getUserName() == null || request.getUserName().trim().isEmpty())
            throw new IllegalArgumentException("Nombre de usuario requerido");
        if (!validateEmail(request.getUserEmail()))
            throw new IllegalArgumentException("Email inválido");
        if (!validatePassword(request.getUserPassword()))
            throw new IllegalArgumentException("Contraseña debe tener al menos 8 caracteres, mayúscula, minúscula y número");
        if (!validateRole(request.getUserRole()))
            throw new IllegalArgumentException("Rol inválido. Valores permitidos: CLIENT, TRANSPORTER, OPERATOR, ADMIN");
        return true;
    }

    @Override
    public boolean validateForUpdate(UserRequest request) {
        if (request == null) throw new IllegalArgumentException("Request no puede ser nulo");
        if (request.getUserName() != null && request.getUserName().trim().isEmpty())
            throw new IllegalArgumentException("Nombre de usuario no puede ser vacío");
        if (request.getUserEmail() != null && !validateEmail(request.getUserEmail()))
            throw new IllegalArgumentException("Email inválido");
        if (request.getUserPassword() != null && !validatePassword(request.getUserPassword()))
            throw new IllegalArgumentException("Contraseña no cumple requisitos");
        if (request.getUserRole() != null && !validateRole(request.getUserRole()))
            throw new IllegalArgumentException("Rol inválido");
        return true;
    }

    @Override
    public boolean validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    @Override
    public boolean validatePassword(String password) {
        if (password == null || password.length() < 8) return false;
        // Al menos una mayúscula, una minúscula, un número
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$");
    }

    @Override
    public boolean validateRole(String role) {
        if (role == null) return false;
        try {
            UserRole.valueOf(role.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
