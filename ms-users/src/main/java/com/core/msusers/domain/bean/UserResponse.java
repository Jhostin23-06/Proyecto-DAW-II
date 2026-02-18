package com.core.msusers.domain.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String userName;
    private String userEmail;
    private String userPassword;
    private String userRole;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean active;

}
