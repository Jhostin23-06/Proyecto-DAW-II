package com.core.msauditoria.infrastructure.messaging;

import com.core.msauditoria.application.port.usecase.RecordAuditEventPort;
import com.core.msauditoria.domain.bean.AuditEventRecordRequest;
import com.core.msauditoria.infrastructure.configuration.CorrelationIdFilter;
import com.core.msauditoria.infrastructure.configuration.RabbitAuditConfig;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditEventListener {

    private static final String JAVA_SERIALIZED_CONTENT_TYPE = "application/x-java-serialized-object";

    private final RecordAuditEventPort recordAuditEventPort;

    @RabbitListener(queues = {
            RabbitAuditConfig.AUDIT_CLIENTS_CREATED_QUEUE,
            RabbitAuditConfig.AUDIT_SHIPMENT_CREATED_QUEUE,
            RabbitAuditConfig.AUDIT_TRANSPORT_CREATED_QUEUE,
            RabbitAuditConfig.AUDIT_TRANSPORT_STATUS_CHANGED_QUEUE,
            RabbitAuditConfig.AUDIT_TRANSPORT_ASSIGNED_QUEUE,
            RabbitAuditConfig.AUDIT_USERS_CREATED_QUEUE,
            RabbitAuditConfig.AUDIT_USERS_UPDATED_QUEUE,
            RabbitAuditConfig.AUDIT_USERS_DELETED_QUEUE
    })
    public void onEvent(Message message, @Header(name = AmqpHeaders.CONSUMER_QUEUE, required = false) String queueName) {
        try {
            MessageProperties props = message.getMessageProperties();
            String exchange = props.getReceivedExchange();
            String routingKey = props.getReceivedRoutingKey();
            String correlationId = resolveCorrelationId(props);
            Instant occurredAt = resolveOccurredAt(props);
            String payload = decodePayload(message.getBody(), props.getContentType(), props.getContentEncoding());

            AuditEventRecordRequest request = new AuditEventRecordRequest(
                    resolveEventSource(exchange),
                    routingKey == null ? "unknown" : routingKey,
                    routingKey,
                    exchange,
                    queueName == null ? props.getConsumerQueue() : queueName,
                    correlationId,
                    payload,
                    occurredAt
            );

            recordAuditEventPort.record(request);
        } catch (Exception ex) {
            log.error("No se pudo registrar evento de auditoria: {}", ex.toString(), ex);
        }
    }

    private String resolveEventSource(String exchange) {
        if (RabbitAuditConfig.CLIENTS_EXCHANGE.equals(exchange)) {
            return "ms-clientes";
        }
        if (RabbitAuditConfig.SERVICES_EXCHANGE.equals(exchange)) {
            return "ms-servicios";
        }
        if (RabbitAuditConfig.TRANSPORT_EXCHANGE.equals(exchange)) {
            return "ms-transportistas";
        }
        if (RabbitAuditConfig.USERS_EXCHANGE.equals(exchange)) {
            return "ms-users";
        }
        return exchange == null || exchange.isBlank() ? "unknown" : exchange;
    }

    private String resolveCorrelationId(MessageProperties props) {
        Object headerCorrelationId = props.getHeaders().get(CorrelationIdFilter.CORRELATION_ID_HEADER);
        if (headerCorrelationId instanceof String headerValue && !headerValue.isBlank()) {
            return headerValue;
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
}
