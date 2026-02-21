# Backend - Guia de arranque

Esta guia permite levantar todo el backend en una maquina nueva usando Docker para infraestructura.

## 1) Prerrequisitos

- Java 21 instalado (Eclipse Temurin/Adoptium recomendado)
- Maven Wrapper (ya incluido en cada microservicio)
- Docker Desktop activo
- Puerto libres: `5432`, `5672`, `15672`, `8761`, `8080`, `8081`, `8082`, `8083`, `8084`

## 2) Levantar infraestructura (PostgreSQL + RabbitMQ)

Desde la raiz del proyecto:

```bash
docker compose -f docker/docker-compose.yml up -d
```

Validar contenedores:

```bash
docker ps
```

Debe aparecer:

- `postgres-transportes`
- `rabbitmq-simple`

## 3) Levantar microservicios

### Opcion A (Linux/Mac/Git Bash)

```bash
./scripts/start-microservices.sh
```

### Opcion B (PowerShell en Windows)

Levantar en este orden (cada comando en terminal aparte o en background):

1. `eureka-server` (8761)
2. `ms-users` (8084)
3. `ms-clientes` (8081)
4. `ms-servicios` (8082)
5. `ms-transportistas` (8083)
6. `api-gateway` (8080)

## 4) Verificacion rapida

- Eureka: `http://localhost:8761`
- Gateway: `http://localhost:8080`
- RabbitMQ UI: `http://localhost:15672`
  - usuario: `admin`
  - password: `admin`

## 5) Login inicial de administrador

`ms-users` crea automaticamente el admin si no existe:

- email: `admin@transportes.local`
- password: `Admin123!`
- role: `ADMIN`

Prueba de login:

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "userEmail": "admin@transportes.local",
  "userPassword": "Admin123!"
}
```

## 6) Apagar todo

### Bash

```bash
./scripts/stop-microservices.sh
```

### Docker infraestructura

```bash
docker compose -f docker/docker-compose.yml down
```

## 7) Si algo falla

- Error `500` en login con mensaje `relation "users" does not exist`:
  - Reiniciar microservicios (especialmente `ms-users`) para que Hibernate cree tablas.
- Si existe PostgreSQL local instalado, evitar conflicto en `5432`:
  - detener servicio local o usar solo Docker.
- Para reconstruir bases desde cero:

```bash
docker compose -f docker/docker-compose.yml down -v
docker compose -f docker/docker-compose.yml up -d
```

## 8) Logs

Todos los logs quedan en:

- `logs/`
