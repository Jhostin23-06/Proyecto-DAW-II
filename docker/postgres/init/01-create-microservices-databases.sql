-- Creates the databases required by all microservices.
-- This script runs only the first time the postgres volume is initialized.

SELECT 'CREATE DATABASE dbusers OWNER postgres'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'dbusers')\gexec

SELECT 'CREATE DATABASE clientes_db OWNER postgres'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'clientes_db')\gexec

SELECT 'CREATE DATABASE dbservicios OWNER postgres'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'dbservicios')\gexec

SELECT 'CREATE DATABASE dbtransportista OWNER postgres'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'dbtransportista')\gexec

SELECT 'CREATE DATABASE dbauditoria OWNER postgres'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'dbauditoria')\gexec

SELECT 'CREATE DATABASE dbnotificaciones OWNER postgres'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'dbnotificaciones')\gexec
