package core.cibertec.ms_servicios.infrastructure.messaging.adapter;

import core.cibertec.ms_servicios.application.port.outservice.ShipmentEventPort;
import core.cibertec.ms_servicios.domain.bean.ShipmentResponse;
import core.cibertec.ms_servicios.config.CorrelationIdFilter;
import core.cibertec.ms_servicios.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShipmentEventAdapter implements ShipmentEventPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishShipmentCreated(ShipmentResponse response) {
        publish(RabbitConfig.ROUTING_CREATED, response, "created");
    }

    @Override
    public void publishShipmentStatusUpdated(ShipmentResponse response) {
        publish(RabbitConfig.ROUTING_STATUS_UPDATED, response, "status.updated");
    }

    private void publish(String routingKey, ShipmentResponse response, String label) {
        try {
            log.info("Publishing shipment {} event id={}", label, response.getShipmentId());
            String correlationId = MDC.get(CorrelationIdFilter.MDC_KEY);
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, routingKey, response, message -> {
                if (correlationId != null && !correlationId.isBlank()) {
                    message.getMessageProperties()
                            .setHeader(CorrelationIdFilter.CORRELATION_ID_HEADER, correlationId);
                }
                return message;
            });
        } catch (Exception e) {
            log.error("Error publishing shipment {} event", label, e);
        }
    }
}
