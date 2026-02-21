package core.cibertec.ms_servicios.application.port.outservice.client;

import core.cibertec.ms_servicios.infrastructure.outservice.dto.TransportSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "ms-transportistas")
public interface TransportFeignClient {

    @GetMapping("/api/transports/{id}")
    TransportSummaryResponse getTransportById(@PathVariable("id") String id);

    @GetMapping("/api/transports")
    List<TransportSummaryResponse> getTransportsByUserId(@RequestParam("userId") String userId);
}
