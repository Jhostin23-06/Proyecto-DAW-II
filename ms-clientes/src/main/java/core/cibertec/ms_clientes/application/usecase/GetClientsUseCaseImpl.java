package core.cibertec.ms_clientes.application.usecase;

import core.cibertec.ms_clientes.application.port.inservice.GetClientsUseCase;
import core.cibertec.ms_clientes.application.port.outservice.ClientRepositoryPort;
import core.cibertec.ms_clientes.domain.model.Client;

import java.util.List;

public class GetClientsUseCaseImpl implements GetClientsUseCase {

    private final ClientRepositoryPort repo;

    public GetClientsUseCaseImpl(ClientRepositoryPort repo) {
        this.repo = repo;
    }

    @Override
    public List<Client> getClients(Long id, String companyCode, String companyName, String address,
                                   String contactName, String email, String phone) {
        return repo.findByFilters(id, companyCode, companyName, address, contactName, email, phone);
    }
}
