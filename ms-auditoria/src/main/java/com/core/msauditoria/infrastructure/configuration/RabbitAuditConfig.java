package com.core.msauditoria.infrastructure.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitAuditConfig {

    public static final String CLIENTS_EXCHANGE = "clients.exchange";
    public static final String CLIENTS_CREATED_ROUTING = "clients.created";
    public static final String AUDIT_CLIENTS_CREATED_QUEUE = "audit.clients.created.queue";

    public static final String SERVICES_EXCHANGE = "services.exchange";
    public static final String SHIPMENT_CREATED_ROUTING = "services.shipment.created";
    public static final String AUDIT_SHIPMENT_CREATED_QUEUE = "audit.services.shipment.created.queue";

    public static final String TRANSPORT_EXCHANGE = "transport.exchange";
    public static final String TRANSPORT_CREATED_ROUTING = "transport.created";
    public static final String TRANSPORT_STATUS_CHANGED_ROUTING = "transport.status.changed";
    public static final String TRANSPORT_ASSIGNED_ROUTING = "transport.assigned";
    public static final String AUDIT_TRANSPORT_CREATED_QUEUE = "audit.transport.created.queue";
    public static final String AUDIT_TRANSPORT_STATUS_CHANGED_QUEUE = "audit.transport.status.changed.queue";
    public static final String AUDIT_TRANSPORT_ASSIGNED_QUEUE = "audit.transport.assigned.queue";

    public static final String USERS_EXCHANGE = "users.exchange";
    public static final String USERS_CREATED_ROUTING = "users.created";
    public static final String USERS_UPDATED_ROUTING = "users.updated";
    public static final String USERS_DELETED_ROUTING = "users.deleted";
    public static final String AUDIT_USERS_CREATED_QUEUE = "audit.users.created.queue";
    public static final String AUDIT_USERS_UPDATED_QUEUE = "audit.users.updated.queue";
    public static final String AUDIT_USERS_DELETED_QUEUE = "audit.users.deleted.queue";

    @Bean
    public TopicExchange clientsExchange() {
        return ExchangeBuilder.topicExchange(CLIENTS_EXCHANGE).durable(true).build();
    }

    @Bean
    public TopicExchange servicesExchange() {
        return ExchangeBuilder.topicExchange(SERVICES_EXCHANGE).durable(true).build();
    }

    @Bean
    public TopicExchange transportExchange() {
        return ExchangeBuilder.topicExchange(TRANSPORT_EXCHANGE).durable(true).build();
    }

    @Bean
    public TopicExchange usersExchange() {
        return ExchangeBuilder.topicExchange(USERS_EXCHANGE).durable(true).build();
    }

    @Bean
    public Queue auditClientsCreatedQueue() {
        return new Queue(AUDIT_CLIENTS_CREATED_QUEUE, true);
    }

    @Bean
    public Queue auditShipmentCreatedQueue() {
        return new Queue(AUDIT_SHIPMENT_CREATED_QUEUE, true);
    }

    @Bean
    public Queue auditTransportCreatedQueue() {
        return new Queue(AUDIT_TRANSPORT_CREATED_QUEUE, true);
    }

    @Bean
    public Queue auditTransportStatusChangedQueue() {
        return new Queue(AUDIT_TRANSPORT_STATUS_CHANGED_QUEUE, true);
    }

    @Bean
    public Queue auditTransportAssignedQueue() {
        return new Queue(AUDIT_TRANSPORT_ASSIGNED_QUEUE, true);
    }

    @Bean
    public Queue auditUsersCreatedQueue() {
        return new Queue(AUDIT_USERS_CREATED_QUEUE, true);
    }

    @Bean
    public Queue auditUsersUpdatedQueue() {
        return new Queue(AUDIT_USERS_UPDATED_QUEUE, true);
    }

    @Bean
    public Queue auditUsersDeletedQueue() {
        return new Queue(AUDIT_USERS_DELETED_QUEUE, true);
    }

    @Bean
    public Binding bindAuditClientsCreated() {
        return BindingBuilder.bind(auditClientsCreatedQueue()).to(clientsExchange()).with(CLIENTS_CREATED_ROUTING);
    }

    @Bean
    public Binding bindAuditShipmentCreated() {
        return BindingBuilder.bind(auditShipmentCreatedQueue()).to(servicesExchange()).with(SHIPMENT_CREATED_ROUTING);
    }

    @Bean
    public Binding bindAuditTransportCreated() {
        return BindingBuilder.bind(auditTransportCreatedQueue()).to(transportExchange()).with(TRANSPORT_CREATED_ROUTING);
    }

    @Bean
    public Binding bindAuditTransportStatusChanged() {
        return BindingBuilder.bind(auditTransportStatusChangedQueue()).to(transportExchange()).with(TRANSPORT_STATUS_CHANGED_ROUTING);
    }

    @Bean
    public Binding bindAuditTransportAssigned() {
        return BindingBuilder.bind(auditTransportAssignedQueue()).to(transportExchange()).with(TRANSPORT_ASSIGNED_ROUTING);
    }

    @Bean
    public Binding bindAuditUsersCreated() {
        return BindingBuilder.bind(auditUsersCreatedQueue()).to(usersExchange()).with(USERS_CREATED_ROUTING);
    }

    @Bean
    public Binding bindAuditUsersUpdated() {
        return BindingBuilder.bind(auditUsersUpdatedQueue()).to(usersExchange()).with(USERS_UPDATED_ROUTING);
    }

    @Bean
    public Binding bindAuditUsersDeleted() {
        return BindingBuilder.bind(auditUsersDeletedQueue()).to(usersExchange()).with(USERS_DELETED_ROUTING);
    }
}
