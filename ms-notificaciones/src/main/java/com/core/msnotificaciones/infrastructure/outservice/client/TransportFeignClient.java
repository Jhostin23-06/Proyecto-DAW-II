package com.core.msnotificaciones.infrastructure.outservice.client;

import com.core.msnotificaciones.infrastructure.outservice.dto.TransportSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-transportistas")
public interface TransportFeignClient {

    @GetMapping("/api/transports/{id}")
    TransportSummaryResponse getTransportById(@PathVariable("id") String id);
}
