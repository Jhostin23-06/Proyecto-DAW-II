package com.core.msusers.domain.bean;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "userEmail is required")
    @Email(message = "userEmail must be a valid email")
    private String userEmail;

    @NotBlank(message = "userPassword is required")
    private String userPassword;
}
