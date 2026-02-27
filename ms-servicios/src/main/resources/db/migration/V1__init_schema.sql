CREATE TABLE IF NOT EXISTS shipment_category (
    category_id BIGINT PRIMARY KEY,
    category_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS shipment_status (
    status_id BIGINT PRIMARY KEY,
    status_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE SEQUENCE IF NOT EXISTS shipment_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS shipments (
    shipment_id BIGINT PRIMARY KEY DEFAULT nextval('shipment_id_seq'),
    category_id BIGINT,
    description VARCHAR(255),
    price DOUBLE PRECISION,
    weight DOUBLE PRECISION,
    volume DOUBLE PRECISION,
    origin VARCHAR(255),
    destination VARCHAR(255),
    client_id BIGINT,
    transport_id VARCHAR(255),
    order_number VARCHAR(255),
    status VARCHAR(255),
    at_date TIMESTAMP,
    status_id BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_shipments_category'
    ) THEN
        ALTER TABLE shipments
            ADD CONSTRAINT fk_shipments_category
            FOREIGN KEY (category_id) REFERENCES shipment_category (category_id);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_shipments_status'
    ) THEN
        ALTER TABLE shipments
            ADD CONSTRAINT fk_shipments_status
            FOREIGN KEY (status_id) REFERENCES shipment_status (status_id);
    END IF;
END $$;

ALTER SEQUENCE shipment_id_seq OWNED BY shipments.shipment_id;

INSERT INTO shipment_category (category_id, category_name) VALUES
    (1, 'DOCUMENTS'),
    (2, 'PACKAGES'),
    (3, 'HEAVY_CARGO'),
    (4, 'FRAGILE')
ON CONFLICT (category_id) DO NOTHING;

INSERT INTO shipment_status (status_id, status_name) VALUES
    (1, 'CREATED'),
    (2, 'ASSIGNED'),
    (3, 'IN_TRANSIT'),
    (4, 'DELIVERED'),
    (5, 'CANCELLED')
ON CONFLICT (status_id) DO NOTHING;
