package core.cibertec.ms_clientes.application.usecase;

import core.cibertec.ms_clientes.application.event.ClientCreatedEvent;
import core.cibertec.ms_clientes.application.port.inservice.CreateClientUseCase;
import core.cibertec.ms_clientes.application.port.outservice.ClientEventPublisherPort;
import core.cibertec.ms_clientes.application.port.outservice.ClientRepositoryPort;
import core.cibertec.ms_clientes.domain.model.Client;

public class CreateClientUseCaseImpl implements CreateClientUseCase {

    private final ClientRepositoryPort repo;
    private final ClientEventPublisherPort eventPublisher;

    public CreateClientUseCaseImpl(ClientRepositoryPort repo,
                               ClientEventPublisherPort eventPublisher) {
    this.repo = repo;
    this.eventPublisher = eventPublisher;
    }


    @Override
    public Client create(Client client) {
        client.validate();

        if (repo.existsByCompanyCode(client.getCompanyCode())) {
            throw new IllegalArgumentException("Ya existe un cliente con ese RUC.");
        }

        // id null -> lo genera la BD (autoincrement)
        client.setId(null);

        Client saved = repo.save(client);

        eventPublisher.publishClientCreated(
                new ClientCreatedEvent(
                        saved.getId(),
                        saved.getCompanyCode(),
                        saved.getCompanyName(),
                        saved.getEmail()
                )
        );

        return saved;

    }
}
