CREATE TABLE IF NOT EXISTS clients (
    id BIGSERIAL PRIMARY KEY,
    company_code VARCHAR(11) NOT NULL,
    company_name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    contact_name VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(255)
);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uk_clients_company_code'
    ) THEN
        ALTER TABLE clients
            ADD CONSTRAINT uk_clients_company_code UNIQUE (company_code);
    END IF;
END $$;
