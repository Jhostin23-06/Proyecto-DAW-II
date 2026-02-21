package core.cibertec.ms_servicios.application.port.outservice.client;

import core.cibertec.ms_servicios.infrastructure.outservice.dto.ClientSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "ms-clientes")
public interface ClientFeignClient {

    @GetMapping("/api/clients")
    List<ClientSummaryResponse> getClients(@RequestParam("id") Long id);
}
