CREATE TABLE IF NOT EXISTS audit_events (
    audit_id BIGSERIAL PRIMARY KEY,
    event_source VARCHAR(80) NOT NULL,
    event_type VARCHAR(150) NOT NULL,
    routing_key VARCHAR(150),
    exchange_name VARCHAR(150),
    queue_name VARCHAR(150),
    correlation_id VARCHAR(100),
    payload TEXT NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    consumed_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_audit_events_consumed_at ON audit_events (consumed_at DESC);
CREATE INDEX IF NOT EXISTS idx_audit_events_event_source ON audit_events (event_source);
CREATE INDEX IF NOT EXISTS idx_audit_events_event_type ON audit_events (event_type);
CREATE INDEX IF NOT EXISTS idx_audit_events_routing_key ON audit_events (routing_key);
CREATE INDEX IF NOT EXISTS idx_audit_events_correlation_id ON audit_events (correlation_id);
