package core.cibertec.ms_servicios.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "services.exchange";
    public static final String ROUTING_CREATED = "services.shipment.created";
    public static final String QUEUE_CREATED = "services.shipment.created.queue";

    @Bean
    public TopicExchange shipmentExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE).durable(true).build();
    }

    @Bean
    public Queue shipmentCreatedQueue() {
        return new Queue(QUEUE_CREATED, true);
    }

    @Bean
    public Binding bindingShipmentCreated(Queue shipmentCreatedQueue, TopicExchange shipmentExchange) {
        return BindingBuilder.bind(shipmentCreatedQueue).to(shipmentExchange).with(ROUTING_CREATED);
    }

}
