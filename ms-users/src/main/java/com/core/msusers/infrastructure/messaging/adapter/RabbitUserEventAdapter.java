package com.core.msusers.infrastructure.messaging.adapter;

import com.core.msusers.application.port.outservice.UserEventPort;
import com.core.msusers.domain.bean.UserResponse;
import com.core.msusers.infrastructure.configuration.CorrelationIdFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitUserEventAdapter implements UserEventPort {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.users}")
    private String usersExchange;

    @Value("${rabbitmq.routing-key.user-created}")
    private String userCreatedRoutingKey;

    @Value("${rabbitmq.routing-key.user-updated}")
    private String userUpdatedRoutingKey;

    @Value("${rabbitmq.routing-key.user-deleted}")
    private String userDeletedRoutingKey;

    @Override
    public void publishUserCreated(UserResponse user) {
        UserAuditEvent payload = UserAuditEvent.from("users.created", user);
        publish(userCreatedRoutingKey, payload, "creado", user.getUserId());
    }

    @Override
    public void publishUserUpdated(UserResponse user) {
        UserAuditEvent payload = UserAuditEvent.from("users.updated", user);
        publish(userUpdatedRoutingKey, payload, "actualizado", user.getUserId());
    }

    @Override
    public void publishUserDeleted(String userId) {
        UserDeletedAuditEvent payload = new UserDeletedAuditEvent(
                userId,
                "users.deleted",
                Instant.now()
        );
        publish(userDeletedRoutingKey, payload, "eliminado", userId);
    }

    private void publish(String routingKey, Object payload, String action, String userId) {
        String correlationId = MDC.get(CorrelationIdFilter.MDC_KEY);
        try {
            rabbitTemplate.convertAndSend(usersExchange, routingKey, payload, message -> {
                if (correlationId != null && !correlationId.isBlank()) {
                    message.getMessageProperties()
                            .setHeader(CorrelationIdFilter.CORRELATION_ID_HEADER, correlationId);
                }
                return message;
            });
            log.info("Evento de usuario {} publicado: userId={}", action, userId);
        } catch (Exception ex) {
            log.error("No se pudo publicar evento de usuario {}: {}", action, ex.getMessage());
        }
    }

    private record UserAuditEvent(
            String eventType,
            String userId,
            String userName,
            String userEmail,
            String userRole,
            Boolean active,
            Instant occurredAt
    ) {
        static UserAuditEvent from(String eventType, UserResponse user) {
            return new UserAuditEvent(
                    eventType,
                    user.getUserId(),
                    user.getUserName(),
                    user.getUserEmail(),
                    user.getUserRole(),
                    user.getActive(),
                    Instant.now()
            );
        }
    }

    private record UserDeletedAuditEvent(
            String userId,
            String eventType,
            Instant occurredAt
    ) {}
}
