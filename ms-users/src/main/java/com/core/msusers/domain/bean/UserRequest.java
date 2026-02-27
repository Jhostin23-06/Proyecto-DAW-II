package com.core.msusers.domain.bean;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "userName is required")
    private String userName;

    @NotBlank(message = "userEmail is required")
    @Email(message = "userEmail must be a valid email")
    private String userEmail;

    @NotBlank(message = "userPassword is required")
    @Size(min = 8, message = "userPassword must contain at least 8 characters")
    private String userPassword;

    @NotBlank(message = "userRole is required")
    private String userRole;  // CLIENT, TRANSPORTER, OPERATOR, ADMIN
}
