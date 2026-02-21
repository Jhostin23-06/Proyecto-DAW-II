package com.transporte.mstransportistas.domain.bean;

import lombok.Data;

@Data
public class UserInfo {
    private String userId;
    private String userRole;
    private boolean active;
}
