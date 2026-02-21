package com.transporte.mstransportistas.infrastructure.messaging.adapter;

import com.netflix.discovery.StatusChangeEvent;
import com.transporte.mstransportistas.application.port.outservice.TransportEventPort;
import com.transporte.mstransportistas.domain.bean.TransportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransportEventAdapter implements TransportEventPort {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.transport}")
    private String transportExchange;

    @Value("${rabbitmq.routing-key.transport-created}")
    private String transportCreatedRoutingKey;

    @Value("${rabbitmq.routing-key.transport-status-changed}")
    private String statusChangedRoutingKey;

    @Value("${rabbitmq.routing-key.transport-assigned}")
    private String transportAssignedRoutingKey;

    @Override
    public void publishTransportCreated(TransportResponse response) {
        try {
            log.info("Publicando evento de transporte creado: ID={}", response.getTransportId());
            rabbitTemplate.convertAndSend(
                    transportExchange,
                    transportCreatedRoutingKey,
                    response
            );
            log.debug("Evento publicado exitosamente");
        } catch (Exception e) {
            log.error("Error publicando evento de transporte creado: {}", e.getMessage());
        }
    }

    @Override
    public void publishTransportStatusChanged(String transportId, String oldStatus, String newStatus, String reason) {
        try {
            log.info("Publicando evento de cambio de estado: {} -> {}", oldStatus, newStatus);

            StatusChangeEvent event = new StatusChangeEvent(
                    transportId,
                    oldStatus,
                    newStatus,
                    reason,
                    System.currentTimeMillis()
            );

            rabbitTemplate.convertAndSend(
                    transportExchange,
                    statusChangedRoutingKey,
                    event
            );
            log.debug("Evento de cambio de estado publicado");
        } catch (Exception e) {
            log.error("Error publicando evento de cambio de estado: {}", e.getMessage());
        }
    }

    @Override
    public void publishTransportAssigned(String transportId, Long shipmentId) {
        try {
            log.info("Publicando evento de transporte asignado: transportId={}, shipmentId={}",
                    transportId, shipmentId);

            AssignmentEvent event = new AssignmentEvent(
                    transportId,
                    shipmentId,
                    System.currentTimeMillis()
            );

            rabbitTemplate.convertAndSend(
                    transportExchange,
                    transportAssignedRoutingKey,
                    event
            );
            log.debug("Evento de asignación publicado");
        } catch (Exception e) {
            log.error("Error publicando evento de asignación: {}", e.getMessage());
        }
    }

    private record StatusChangeEvent(
            String transportId,
            String oldStatus,
            String newStatus,
            String reason,
            long timestamp
    ) {}

    private record AssignmentEvent(
            String transportId,
            Long shipmentId,
            long timestamp
    ) {}
}
