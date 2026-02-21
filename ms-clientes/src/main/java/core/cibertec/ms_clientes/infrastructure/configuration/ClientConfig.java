package core.cibertec.ms_clientes.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import core.cibertec.ms_clientes.application.port.inservice.CreateClientUseCase;
import core.cibertec.ms_clientes.application.port.inservice.DeleteClientUseCase;
import core.cibertec.ms_clientes.application.port.inservice.GetClientsUseCase;
import core.cibertec.ms_clientes.application.port.inservice.PatchClientUseCase;
import core.cibertec.ms_clientes.application.port.outservice.ClientEventPublisherPort;
import core.cibertec.ms_clientes.application.port.outservice.ClientRepositoryPort;
import core.cibertec.ms_clientes.application.usecase.CreateClientUseCaseImpl;
import core.cibertec.ms_clientes.application.usecase.DeleteClientUseCaseImpl;
import core.cibertec.ms_clientes.application.usecase.GetClientsUseCaseImpl;
import core.cibertec.ms_clientes.application.usecase.PatchClientUseCaseImpl;
import core.cibertec.ms_clientes.infrastructure.persistence.adapter.ClientPersistenceAdapter;
import core.cibertec.ms_clientes.infrastructure.persistence.repository.JpaClientRepository;

@Configuration
public class ClientConfig {

    @Bean
    ClientRepositoryPort clientRepositoryPort(JpaClientRepository jpaRepo) {
        return new ClientPersistenceAdapter(jpaRepo);
    }

    @Bean
    CreateClientUseCase createClientUseCase(ClientRepositoryPort port,
                                            ClientEventPublisherPort eventPublisher) {
        return new CreateClientUseCaseImpl(port, eventPublisher);
    }

    @Bean
    GetClientsUseCase getClientsUseCase(ClientRepositoryPort port) {
        return new GetClientsUseCaseImpl(port);
    }

    @Bean
    PatchClientUseCase patchClientUseCase(ClientRepositoryPort port) {
        return new PatchClientUseCaseImpl(port);
    }

    @Bean
    DeleteClientUseCase deleteClientUseCase(ClientRepositoryPort port) {
        return new DeleteClientUseCaseImpl(port);
    }
}
