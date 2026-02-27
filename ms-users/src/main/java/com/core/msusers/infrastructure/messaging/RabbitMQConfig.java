package com.core.msusers.infrastructure.messaging;

import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.users}")
    private String usersExchangeName;

    @Bean
    public TopicExchange usersExchange() {
        return ExchangeBuilder.topicExchange(usersExchangeName).durable(true).build();
    }
}
