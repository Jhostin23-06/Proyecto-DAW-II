# Infra local con Docker

Este proyecto usa Docker Compose para levantar infraestructura compartida:

- PostgreSQL: `localhost:5432`
- RabbitMQ: `localhost:5672`
- RabbitMQ Management: `http://localhost:15672`

## Levantar infraestructura

Desde la raiz del proyecto:

```bash
cd docker
docker compose up -d
```

## Base de datos PostgreSQL

Credenciales por defecto:

- Usuario: `postgres`
- Password: `1234`
- Base inicial: `postgres`

Al primer arranque (volumen nuevo), se crean automaticamente estas bases:

- `dbusers`
- `clientes_db`
- `dbservicios`
- `dbtransportista`

Script de inicializacion:

- `docker/postgres/init/01-create-microservices-databases.sql`

## Usuario admin de la aplicacion

`ms-users` crea automaticamente un usuario admin al iniciar (si no existe):

- Email: `admin@transportes.local`
- Password: `Admin123!`
- Rol: `ADMIN`

Configuracion en:

- `ms-users/src/main/resources/application.yml`
- `app.seed.admin.*`

## Re-ejecutar inicializacion de PostgreSQL

Los scripts de `docker-entrypoint-initdb.d` solo corren cuando el volumen es nuevo.
Si necesitas recrear todo desde cero:

```bash
cd docker
docker compose down -v
docker compose up -d
```
