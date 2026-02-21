package com.transporte.mstransportistas.application.port.outservice;

import com.transporte.mstransportistas.domain.bean.UserInfo;

public interface UserServicePort {
    UserInfo getUserById(String userId);
}
