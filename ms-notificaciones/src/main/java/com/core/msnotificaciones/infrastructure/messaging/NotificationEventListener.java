package com.core.msnotificaciones.infrastructure.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.core.msnotificaciones.application.service.NotificationService;
import com.core.msnotificaciones.infrastructure.configuration.CorrelationIdFilter;
import com.core.msnotificaciones.infrastructure.configuration.RabbitNotificationConfig;
import com.core.msnotificaciones.infrastructure.outservice.TransportUserResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private static final String JAVA_SERIALIZED_CONTENT_TYPE = "application/x-java-serialized-object";
    private static final Set<String> RELEVANT_EVENT_TYPES = Set.of(
            "services.shipment.created",
            "services.shipment.status.updated",
            "transport.status.changed",
            "transport.assigned"
    );

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;
    private final TransportUserResolver transportUserResolver;

    @RabbitListener(queues = {
            RabbitNotificationConfig.NOTIF_CLIENTS_CREATED_QUEUE,
            RabbitNotificationConfig.NOTIF_SHIPMENT_CREATED_QUEUE,
            RabbitNotificationConfig.NOTIF_SHIPMENT_STATUS_UPDATED_QUEUE,
            RabbitNotificationConfig.NOTIF_TRANSPORT_CREATED_QUEUE,
            RabbitNotificationConfig.NOTIF_TRANSPORT_STATUS_CHANGED_QUEUE,
            RabbitNotificationConfig.NOTIF_TRANSPORT_ASSIGNED_QUEUE,
            RabbitNotificationConfig.NOTIF_USERS_CREATED_QUEUE,
            RabbitNotificationConfig.NOTIF_USERS_UPDATED_QUEUE,
            RabbitNotificationConfig.NOTIF_USERS_DELETED_QUEUE
    })
    public void onEvent(Message message, @Header(name = AmqpHeaders.CONSUMER_QUEUE, required = false) String queueName) {
        try {
            MessageProperties props = message.getMessageProperties();
            String exchange = props.getReceivedExchange();
            String routingKey = props.getReceivedRoutingKey();
            String correlationId = resolveCorrelationId(props);
            String payload = decodePayload(message.getBody(), props.getContentType(), props.getContentEncoding());
            Instant occurredAt = resolveOccurredAt(props);

            String eventSource = resolveEventSource(exchange);
            String eventType = routingKey == null ? "unknown" : routingKey;
            if (!RELEVANT_EVENT_TYPES.contains(eventType)) {
                return;
            }

            String targetUserId = resolveTargetUserId(eventType, payload);
            if (targetUserId == null || targetUserId.isBlank()) {
                log.debug("Evento {} omitido: no se resolvio transportUserId", eventType);
                return;
            }

            NotificationMessage messageData = buildMessage(eventType, payload);

            notificationService.recordEvent(new NotificationService.NotificationEventData(
                    eventSource,
                    eventType,
                    routingKey,
                    exchange,
                    queueName == null ? props.getConsumerQueue() : queueName,
                    messageData.title(),
                    messageData.message(),
                    payload,
                    correlationId,
                    targetUserId,
                    occurredAt
            ));
        } catch (Exception ex) {
            log.error("No se pudo registrar notificacion desde evento: {}", ex.toString(), ex);
        }
    }

    private NotificationMessage buildMessage(String eventType, String payload) {
        JsonNode root = parseJson(payload);
        return switch (eventType) {
            case "services.shipment.created" -> {
                String shipmentId = readText(root, "shipmentId");
                String orderNumber = readText(root, "orderNumber");
                String destination = readText(root, "destination");
                String title = shipmentId == null
                        ? "Nuevo envio creado"
                        : "Envio #" + shipmentId + " creado";
                String message = "Se creo "
                        + describeShipment(shipmentId, orderNumber)
                        + (destination == null ? "." : " con destino a " + destination + ".");
                yield new NotificationMessage(title, message);
            }
            case "services.shipment.status.updated" -> {
                String shipmentId = readText(root, "shipmentId");
                String status = humanizeStatus(readText(root, "status"));
                String title = shipmentId == null
                        ? "Estado de envio actualizado"
                        : "Envio #" + shipmentId + " actualizado";
                String message = "El estado de "
                        + describeShipment(shipmentId, readText(root, "orderNumber"))
                        + " cambio a " + (status == null ? "un nuevo estado" : status) + ".";
                yield new NotificationMessage(title, message);
            }
            case "transport.status.changed" -> {
                String transportId = readText(root, "transportId");
                String oldStatus = humanizeStatus(readText(root, "oldStatus"));
                String newStatus = humanizeStatus(readText(root, "newStatus"));
                String reason = readText(root, "reason");
                String title = transportId == null
                        ? "Cambio de estado de transporte"
                        : "Transporte " + shortId(transportId) + " actualizado";
                String message = "Tu transporte "
                        + (transportId == null ? "" : shortId(transportId) + " ")
                        + "cambio de " + valueOrFallback(oldStatus, "un estado anterior")
                        + " a " + valueOrFallback(newStatus, "un nuevo estado")
                        + (reason == null ? "." : ". Motivo: " + reason + ".");
                yield new NotificationMessage(title, message);
            }
            case "transport.assigned" -> {
                String transportId = readText(root, "transportId");
                String title = "Transporte asignado";
                String message = "Se te asigno el transporte "
                        + (transportId == null ? "disponible" : shortId(transportId))
                        + " para tus proximos envios.";
                yield new NotificationMessage(title, message);
            }
            default -> new NotificationMessage(
                    "Nuevo evento",
                    "Se recibio una nueva notificacion del sistema."
            );
        };
    }

    private String resolveEventSource(String exchange) {
        if (RabbitNotificationConfig.CLIENTS_EXCHANGE.equals(exchange)) {
            return "ms-clientes";
        }
        if (RabbitNotificationConfig.SERVICES_EXCHANGE.equals(exchange)) {
            return "ms-servicios";
        }
        if (RabbitNotificationConfig.TRANSPORT_EXCHANGE.equals(exchange)) {
            return "ms-transportistas";
        }
        if (RabbitNotificationConfig.USERS_EXCHANGE.equals(exchange)) {
            return "ms-users";
        }
        return exchange == null || exchange.isBlank() ? "unknown" : exchange;
    }

    private String resolveTargetUserId(String eventType, String payload) {
        JsonNode root = parseJson(payload);
        if (root == null) {
            return null;
        }

        if ("services.shipment.created".equals(eventType) || "services.shipment.status.updated".equals(eventType)) {
            return resolveByTransportId(readText(root, "transportId"));
        }

        if ("transport.status.changed".equals(eventType) || "transport.assigned".equals(eventType)) {
            String fromPayload = readText(root, "transportUserId");
            if (fromPayload != null && !fromPayload.isBlank()) {
                return fromPayload;
            }
            return resolveByTransportId(readText(root, "transportId"));
        }

        return null;
    }

    private String resolveByTransportId(String transportId) {
        if (transportId == null || transportId.isBlank()) {
            return null;
        }
        return transportUserResolver.resolveTransportUserId(transportId);
    }

    private JsonNode parseJson(String payload) {
        if (payload == null || payload.isBlank() || payload.startsWith("base64:")) {
            return null;
        }
        try {
            return objectMapper.readTree(payload);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String readText(JsonNode node, String fieldName) {
        if (node == null) {
            return null;
        }
        JsonNode value = node.get(fieldName);
        if (value == null || value.isNull()) {
            return null;
        }
        String text = value.asText();
        return text == null || text.isBlank() ? null : text.trim();
    }

    private String describeShipment(String shipmentId, String orderNumber) {
        if (shipmentId != null && orderNumber != null) {
            return "el envio #" + shipmentId + " (" + orderNumber + ")";
        }
        if (shipmentId != null) {
            return "el envio #" + shipmentId;
        }
        if (orderNumber != null) {
            return "el envio " + orderNumber;
        }
        return "un envio";
    }

    private String shortId(String rawId) {
        if (rawId == null || rawId.isBlank()) {
            return "N/A";
        }
        return rawId.length() <= 8 ? rawId : rawId.substring(0, 8).toUpperCase();
    }

    private String humanizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        return switch (status.trim().toUpperCase()) {
            case "CREATED" -> "Creado";
            case "ASSIGNED" -> "Asignado";
            case "IN_TRANSIT" -> "En transito";
            case "DELIVERED" -> "Entregado";
            case "CANCELLED" -> "Cancelado";
            case "RETURNED" -> "Devuelto";
            case "AVAILABLE" -> "Disponible";
            case "MAINTENANCE" -> "Mantenimiento";
            case "OUT_OF_SERVICE" -> "Fuera de servicio";
            default -> status;
        };
    }

    private String valueOrFallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String resolveCorrelationId(MessageProperties props) {
        Object headerCorrelationId = props.getHeaders().get(CorrelationIdFilter.CORRELATION_ID_HEADER);
        if (headerCorrelationId instanceof String value && !value.isBlank()) {
            return value;
        }
        String correlationId = props.getCorrelationId();
        if (correlationId != null && !correlationId.isBlank()) {
            return correlationId;
        }
        return "N/A";
    }

    private Instant resolveOccurredAt(MessageProperties props) {
        Date timestamp = props.getTimestamp();
        return timestamp == null ? Instant.now() : timestamp.toInstant();
    }

    private String decodePayload(byte[] body, String contentType, String contentEncoding) {
        if (body == null || body.length == 0) {
            return "";
        }

        String normalizedContentType = contentType == null ? "" : contentType.toLowerCase();
        if (normalizedContentType.startsWith("text/") || normalizedContentType.contains("json")) {
            Charset charset = resolveCharset(contentEncoding);
            return new String(body, charset);
        }

        if (JAVA_SERIALIZED_CONTENT_TYPE.equalsIgnoreCase(contentType)) {
            return "base64:" + Base64.getEncoder().encodeToString(body);
        }

        if (isLikelyUtf8(body)) {
            return new String(body, StandardCharsets.UTF_8);
        }
        return "base64:" + Base64.getEncoder().encodeToString(body);
    }

    private Charset resolveCharset(String contentEncoding) {
        if (contentEncoding == null || contentEncoding.isBlank()) {
            return StandardCharsets.UTF_8;
        }
        try {
            return Charset.forName(contentEncoding);
        } catch (Exception ignored) {
            return StandardCharsets.UTF_8;
        }
    }

    private boolean isLikelyUtf8(byte[] bytes) {
        int inspected = Math.min(bytes.length, 128);
        for (int i = 0; i < inspected; i++) {
            int b = bytes[i] & 0xFF;
            if (b == 0) {
                return false;
            }
        }
        return true;
    }

    private record NotificationMessage(String title, String message) {
    }
}
