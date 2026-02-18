package com.core.msusers.domain.contraints;

import com.core.msusers.domain.bean.UserRequest;

public interface UserValidation {
    boolean validateForCreation(UserRequest request);
    boolean validateForUpdate(UserRequest request);
    boolean validateEmail(String email);
    boolean validatePassword(String password);
    boolean validateRole(String role);
}
