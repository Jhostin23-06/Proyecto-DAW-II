package core.cibertec.ms_clientes.application.port.outservice;

import core.cibertec.ms_clientes.application.event.ClientCreatedEvent;

public interface ClientEventPublisherPort {
    void publishClientCreated(ClientCreatedEvent event);
}
