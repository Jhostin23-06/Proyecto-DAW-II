package core.cibertec.ms_clientes.infrastructure.messaging;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "clients.exchange";
    public static final String ROUTING_KEY = "clients.created";
    public static final String QUEUE = "clients.created.queue";

    @Bean
    public TopicExchange clientsExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue clientsCreatedQueue() {
        return QueueBuilder.durable(QUEUE).build();
    }

    @Bean
    public Binding clientsCreatedBinding(Queue clientsCreatedQueue, TopicExchange clientsExchange) {
        return BindingBuilder
                .bind(clientsCreatedQueue)
                .to(clientsExchange)
                .with(ROUTING_KEY);
    }
}
