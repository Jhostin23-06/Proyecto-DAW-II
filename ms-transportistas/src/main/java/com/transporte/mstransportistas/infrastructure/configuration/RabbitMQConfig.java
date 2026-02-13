package com.transporte.mstransportistas.infrastructure.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.transport}")
    private String transportExchange;

    @Value("${rabbitmq.queue.transport-created}")
    private String transportCreatedQueue;

    @Value("${rabbitmq.queue.transport-status-changed}")
    private String transportStatusChangedQueue;

    @Value("${rabbitmq.queue.transport-assigned}")
    private String transportAssignedQueue;

    @Value("${rabbitmq.routing-key.transport-created}")
    private String transportCreatedRoutingKey;

    @Value("${rabbitmq.routing-key.transport-status-changed}")
    private String transportStatusChangedRoutingKey;

    @Value("${rabbitmq.routing-key.transport-assigned}")
    private String transportAssignedRoutingKey;

    @Bean
    public TopicExchange transportExchange() {
        return new TopicExchange(transportExchange);
    }

    @Bean
    public Queue transportCreatedQueue() {
        return new Queue(transportCreatedQueue, true);
    }

    @Bean
    public Queue transportStatusChangedQueue() {
        return new Queue(transportStatusChangedQueue, true);
    }

    @Bean
    public Queue transportAssignedQueue() {
        return new Queue(transportAssignedQueue, true);
    }

    @Bean
    public Binding transportCreatedBinding() {
        return BindingBuilder
                .bind(transportCreatedQueue())
                .to(transportExchange())
                .with(transportCreatedRoutingKey);
    }

    @Bean
    public Binding transportStatusChangedBinding() {
        return BindingBuilder
                .bind(transportStatusChangedQueue())
                .to(transportExchange())
                .with(transportStatusChangedRoutingKey);
    }

    @Bean
    public Binding transportAssignedBinding() {
        return BindingBuilder
                .bind(transportAssignedQueue())
                .to(transportExchange())
                .with(transportAssignedRoutingKey);
    }

}
