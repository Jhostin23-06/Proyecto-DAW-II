CREATE TABLE IF NOT EXISTS transports (
    transport_id VARCHAR(36) PRIMARY KEY,
    transport_user_id VARCHAR(36),
    transport_type VARCHAR(50) NOT NULL,
    transport_capacity DOUBLE PRECISION NOT NULL,
    transport_status VARCHAR(20) NOT NULL,
    transport_location VARCHAR(200),
    transport_driver VARCHAR(100) NOT NULL,
    transport_license_plate VARCHAR(20) NOT NULL,
    transport_company VARCHAR(100) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_license_plate ON transports (transport_license_plate);
CREATE INDEX IF NOT EXISTS idx_transport_status ON transports (transport_status);
CREATE INDEX IF NOT EXISTS idx_transport_user ON transports (transport_user_id);
