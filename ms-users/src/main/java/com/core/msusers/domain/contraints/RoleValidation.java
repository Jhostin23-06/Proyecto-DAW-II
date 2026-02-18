package com.core.msusers.domain.contraints;

import com.core.msusers.domain.model.UserRole;

public interface RoleValidation {
    boolean isValidRole(String role);
    UserRole fromString(String role);
}
