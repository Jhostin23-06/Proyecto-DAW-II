package com.core.msusers.domain.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String token;
    private String type = "Bearer";
    private String userId;
    private String userName;
    private String userEmail;
    private String userRole;
}
