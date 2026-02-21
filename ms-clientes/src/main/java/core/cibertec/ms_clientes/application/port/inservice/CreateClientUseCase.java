package core.cibertec.ms_clientes.application.port.inservice;

import core.cibertec.ms_clientes.domain.model.Client;

public interface CreateClientUseCase {
    Client create(Client client);
}
