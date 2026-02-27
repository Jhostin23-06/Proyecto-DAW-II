package core.cibertec.ms_clientes.application.usecase;

import core.cibertec.ms_clientes.application.port.inservice.DeleteClientUseCase;
import core.cibertec.ms_clientes.application.port.outservice.ClientRepositoryPort;
import core.cibertec.ms_clientes.domain.exception.ClientNotFoundException;

public class DeleteClientUseCaseImpl implements DeleteClientUseCase {

    private final ClientRepositoryPort repo;

    public DeleteClientUseCaseImpl(ClientRepositoryPort repo) {
        this.repo = repo;
    }

    @Override
    public void delete(Long id) {
        if (repo.findById(id).isEmpty()) {
            throw new ClientNotFoundException("Cliente no encontrado.");
        }
        repo.deleteById(id);
    }
}
