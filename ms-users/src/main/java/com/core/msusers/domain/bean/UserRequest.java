package com.core.msusers.domain.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userName;
    private String userEmail;
    private String userPassword;
    private String userRole;  // CLIENT, TRANSPORTER, OPERATOR, ADMIN
}
