package com.core.msnotificaciones.infrastructure.configuration;

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
public class RabbitNotificationConfig {

    public static final String CLIENTS_EXCHANGE = "clients.exchange";
    public static final String CLIENTS_CREATED_ROUTING = "clients.created";
    public static final String NOTIF_CLIENTS_CREATED_QUEUE = "notifications.clients.created.queue";

    public static final String SERVICES_EXCHANGE = "services.exchange";
    public static final String SHIPMENT_CREATED_ROUTING = "services.shipment.created";
    public static final String SHIPMENT_STATUS_UPDATED_ROUTING = "services.shipment.status.updated";
    public static final String NOTIF_SHIPMENT_CREATED_QUEUE = "notifications.services.shipment.created.queue";
    public static final String NOTIF_SHIPMENT_STATUS_UPDATED_QUEUE = "notifications.services.shipment.status.updated.queue";

    public static final String TRANSPORT_EXCHANGE = "transport.exchange";
    public static final String TRANSPORT_CREATED_ROUTING = "transport.created";
    public static final String TRANSPORT_STATUS_CHANGED_ROUTING = "transport.status.changed";
    public static final String TRANSPORT_ASSIGNED_ROUTING = "transport.assigned";
    public static final String NOTIF_TRANSPORT_CREATED_QUEUE = "notifications.transport.created.queue";
    public static final String NOTIF_TRANSPORT_STATUS_CHANGED_QUEUE = "notifications.transport.status.changed.queue";
    public static final String NOTIF_TRANSPORT_ASSIGNED_QUEUE = "notifications.transport.assigned.queue";

    public static final String USERS_EXCHANGE = "users.exchange";
    public static final String USERS_CREATED_ROUTING = "users.created";
    public static final String USERS_UPDATED_ROUTING = "users.updated";
    public static final String USERS_DELETED_ROUTING = "users.deleted";
    public static final String NOTIF_USERS_CREATED_QUEUE = "notifications.users.created.queue";
    public static final String NOTIF_USERS_UPDATED_QUEUE = "notifications.users.updated.queue";
    public static final String NOTIF_USERS_DELETED_QUEUE = "notifications.users.deleted.queue";

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
    public Queue notifClientsCreatedQueue() {
        return new Queue(NOTIF_CLIENTS_CREATED_QUEUE, true);
    }

    @Bean
    public Queue notifShipmentCreatedQueue() {
        return new Queue(NOTIF_SHIPMENT_CREATED_QUEUE, true);
    }

    @Bean
    public Queue notifShipmentStatusUpdatedQueue() {
        return new Queue(NOTIF_SHIPMENT_STATUS_UPDATED_QUEUE, true);
    }

    @Bean
    public Queue notifTransportCreatedQueue() {
        return new Queue(NOTIF_TRANSPORT_CREATED_QUEUE, true);
    }

    @Bean
    public Queue notifTransportStatusChangedQueue() {
        return new Queue(NOTIF_TRANSPORT_STATUS_CHANGED_QUEUE, true);
    }

    @Bean
    public Queue notifTransportAssignedQueue() {
        return new Queue(NOTIF_TRANSPORT_ASSIGNED_QUEUE, true);
    }

    @Bean
    public Queue notifUsersCreatedQueue() {
        return new Queue(NOTIF_USERS_CREATED_QUEUE, true);
    }

    @Bean
    public Queue notifUsersUpdatedQueue() {
        return new Queue(NOTIF_USERS_UPDATED_QUEUE, true);
    }

    @Bean
    public Queue notifUsersDeletedQueue() {
        return new Queue(NOTIF_USERS_DELETED_QUEUE, true);
    }

    @Bean
    public Binding bindNotifClientsCreated() {
        return BindingBuilder.bind(notifClientsCreatedQueue()).to(clientsExchange()).with(CLIENTS_CREATED_ROUTING);
    }

    @Bean
    public Binding bindNotifShipmentCreated() {
        return BindingBuilder.bind(notifShipmentCreatedQueue()).to(servicesExchange()).with(SHIPMENT_CREATED_ROUTING);
    }

    @Bean
    public Binding bindNotifShipmentStatusUpdated() {
        return BindingBuilder.bind(notifShipmentStatusUpdatedQueue()).to(servicesExchange()).with(SHIPMENT_STATUS_UPDATED_ROUTING);
    }

    @Bean
    public Binding bindNotifTransportCreated() {
        return BindingBuilder.bind(notifTransportCreatedQueue()).to(transportExchange()).with(TRANSPORT_CREATED_ROUTING);
    }

    @Bean
    public Binding bindNotifTransportStatusChanged() {
        return BindingBuilder.bind(notifTransportStatusChangedQueue()).to(transportExchange()).with(TRANSPORT_STATUS_CHANGED_ROUTING);
    }

    @Bean
    public Binding bindNotifTransportAssigned() {
        return BindingBuilder.bind(notifTransportAssignedQueue()).to(transportExchange()).with(TRANSPORT_ASSIGNED_ROUTING);
    }

    @Bean
    public Binding bindNotifUsersCreated() {
        return BindingBuilder.bind(notifUsersCreatedQueue()).to(usersExchange()).with(USERS_CREATED_ROUTING);
    }

    @Bean
    public Binding bindNotifUsersUpdated() {
        return BindingBuilder.bind(notifUsersUpdatedQueue()).to(usersExchange()).with(USERS_UPDATED_ROUTING);
    }

    @Bean
    public Binding bindNotifUsersDeleted() {
        return BindingBuilder.bind(notifUsersDeletedQueue()).to(usersExchange()).with(USERS_DELETED_ROUTING);
    }
}
