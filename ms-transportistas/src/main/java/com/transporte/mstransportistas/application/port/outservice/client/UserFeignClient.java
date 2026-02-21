package com.transporte.mstransportistas.application.port.outservice.client;

import com.transporte.mstransportistas.domain.bean.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-users", path = "/api/users")
public interface UserFeignClient {
    @GetMapping("/{id}")
    UserInfo getUserById(@PathVariable("id") String id);
}
