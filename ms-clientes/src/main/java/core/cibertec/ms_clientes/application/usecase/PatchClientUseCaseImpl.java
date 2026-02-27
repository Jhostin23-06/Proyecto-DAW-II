package core.cibertec.ms_clientes.application.usecase;

import core.cibertec.ms_clientes.application.port.inservice.PatchClientUseCase;
import core.cibertec.ms_clientes.application.port.outservice.ClientRepositoryPort;
import core.cibertec.ms_clientes.domain.exception.ClientNotFoundException;
import core.cibertec.ms_clientes.domain.model.Client;

public class PatchClientUseCaseImpl implements PatchClientUseCase {

    private final ClientRepositoryPort repo;

    public PatchClientUseCaseImpl(ClientRepositoryPort repo) {
        this.repo = repo;
    }

    @Override
    public Client patch(Long id, String companyCode, String companyName, String address,
                        String contactName, String email, String phone) {

        Client client = repo.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado."));

        // Si van a cambiar el RUC, esto validará duplicados
        if (companyCode != null && !companyCode.equals(client.getCompanyCode())
                && repo.existsByCompanyCode(companyCode)) {
            throw new IllegalArgumentException("Ya existe un cliente con ese RUC.");
        }

        client.patch(companyCode, companyName, address, contactName, email, phone);
        client.validate();

        return repo.save(client);
    }
}
