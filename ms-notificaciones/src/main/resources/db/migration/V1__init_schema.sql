CREATE TABLE IF NOT EXISTS notifications (
    notification_id BIGSERIAL PRIMARY KEY,
    event_source VARCHAR(80) NOT NULL,
    event_type VARCHAR(120) NOT NULL,
    routing_key VARCHAR(120),
    exchange_name VARCHAR(120),
    queue_name VARCHAR(120),
    title VARCHAR(200) NOT NULL,
    message_text VARCHAR(500) NOT NULL,
    payload TEXT,
    correlation_id VARCHAR(120),
    occurred_at TIMESTAMP NOT NULL,
    received_at TIMESTAMP NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    read_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_notifications_event_source ON notifications(event_source);
CREATE INDEX IF NOT EXISTS idx_notifications_is_read ON notifications(is_read);
CREATE INDEX IF NOT EXISTS idx_notifications_received_at ON notifications(received_at DESC);
