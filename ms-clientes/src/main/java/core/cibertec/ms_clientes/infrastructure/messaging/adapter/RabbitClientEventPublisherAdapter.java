package core.cibertec.ms_clientes.infrastructure.messaging.adapter;

import core.cibertec.ms_clientes.application.event.ClientCreatedEvent;
import core.cibertec.ms_clientes.application.port.outservice.ClientEventPublisherPort;
import core.cibertec.ms_clientes.infrastructure.messaging.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitClientEventPublisherAdapter implements ClientEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    public RabbitClientEventPublisherAdapter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishClientCreated(ClientCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                event
        );
    }
}
