package core.cibertec.ms_clientes.infrastructure.controller;

import org.springframework.web.bind.annotation.*;
import core.cibertec.ms_clientes.application.port.inservice.CreateClientUseCase;
import core.cibertec.ms_clientes.application.port.inservice.DeleteClientUseCase;
import core.cibertec.ms_clientes.application.port.inservice.GetClientsUseCase;
import core.cibertec.ms_clientes.application.port.inservice.PatchClientUseCase;
import core.cibertec.ms_clientes.domain.model.Client;
import core.cibertec.ms_clientes.infrastructure.controller.dto.ClientCreateRequest;
import core.cibertec.ms_clientes.infrastructure.controller.dto.ClientPatchRequest;
import core.cibertec.ms_clientes.infrastructure.controller.dto.ClientResponse;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final CreateClientUseCase createUseCase;
    private final GetClientsUseCase getUseCase;
    private final PatchClientUseCase patchUseCase;
    private final DeleteClientUseCase deleteUseCase;

    public ClientController(CreateClientUseCase createUseCase, GetClientsUseCase getUseCase,
                            PatchClientUseCase patchUseCase, DeleteClientUseCase deleteUseCase) {
        this.createUseCase = createUseCase;
        this.getUseCase = getUseCase;
        this.patchUseCase = patchUseCase;
        this.deleteUseCase = deleteUseCase;
    }

    @GetMapping
    public List<ClientResponse> getClients(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String companyCode,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String contactName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone
    ) {
        return getUseCase.getClients(id, companyCode, companyName, address, contactName, email, phone)
                .stream().map(this::toResponse).toList();
    }

    @PostMapping
    public ClientResponse create(@RequestBody ClientCreateRequest req) {
        Client created = createUseCase.create(
                new Client(null, req.companyCode(), req.companyName(), req.address(),
                        req.contactName(), req.email(), req.phone())
        );
        return toResponse(created);
    }

    @PatchMapping("/{id}")
    public ClientResponse patch(@PathVariable Long id, @RequestBody ClientPatchRequest req) {
        Client updated = patchUseCase.patch(
                id, req.companyCode(), req.companyName(), req.address(),
                req.contactName(), req.email(), req.phone()
        );
        return toResponse(updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        deleteUseCase.delete(id);
    }

    private ClientResponse toResponse(Client c) {
        return new ClientResponse(
                c.getId(), c.getCompanyCode(), c.getCompanyName(), c.getAddress(),
                c.getContactName(), c.getEmail(), c.getPhone()
        );
    }
}
