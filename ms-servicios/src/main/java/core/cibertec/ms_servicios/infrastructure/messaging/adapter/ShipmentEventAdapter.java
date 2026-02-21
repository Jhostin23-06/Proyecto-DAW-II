package core.cibertec.ms_servicios.infrastructure.messaging.adapter;

import core.cibertec.ms_servicios.application.port.outservice.ShipmentEventPort;
import core.cibertec.ms_servicios.domain.bean.ShipmentResponse;
import core.cibertec.ms_servicios.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShipmentEventAdapter implements ShipmentEventPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishShipmentCreated(ShipmentResponse response) {
        try {
            log.info("Publishing shipment created event id={}", response.getShipmentId());
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_CREATED, response);
        } catch (Exception e) {
            log.error("Error publishing shipment created event", e);
        }
    }
}
