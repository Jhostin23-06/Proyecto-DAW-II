# Proyecto Transportes - Backend

Backend distribuido basado en microservicios para la gestion de usuarios, clientes, transportes y envios.

## 1) Vision General

El backend esta compuesto por los siguientes servicios:

- `api-gateway` (puerto `8080`): punto de entrada unico para el frontend.
- `eureka-server` (puerto `8761`): descubrimiento de servicios.
- `ms-users` (puerto `8084`): autenticacion JWT y gestion de usuarios.
- `ms-clientes` (puerto `8081`): gestion de clientes.
- `ms-servicios` (puerto `8082`): gestion de envios y catalogos.
- `ms-transportistas` (puerto `8083`): gestion de transportes y asignaciones.
- `ms-auditoria` (puerto `8085`): consumo de eventos y consulta de trazabilidad.
- `ms-notificaciones` (puerto `8086`): bandeja de notificaciones para frontend.

Infraestructura local:

- PostgreSQL (`5432`)
- RabbitMQ (`5672` + UI `15672`)

## 2) Arquitectura Hexagonal

Los microservicios de dominio (`ms-clientes`, `ms-servicios`, `ms-transportistas`, `ms-users`, `ms-auditoria`, `ms-notificaciones`) siguen organizacion por capas tipo hexagonal:

- `domain`: entidades/modelos de dominio, reglas y excepciones.
- `application`: casos de uso y puertos (`in` / `out`).
- `infrastructure`: adaptadores de entrada/salida (REST controllers, JPA, Feign, RabbitMQ, configuracion).

Esta estructura desacopla logica de negocio de frameworks e integraciones externas.

## 3) Tecnologias del Proyecto

- Java 21
- Spring Boot 4
- Spring Cloud (Eureka, Gateway, OpenFeign)
- Spring Data JPA (Hibernate)
- PostgreSQL
- Flyway (migraciones DB)
- RabbitMQ (eventos asincronos)
- Spring Security + JWT (`ms-users`)
- Resilience4j (Circuit Breaker / Retry)
- Spring Boot Actuator
- Micrometer + Prometheus
- ModelMapper
- Lombok
- Docker Compose

## 4) Resiliencia (Circuit Breaker + Retry)

### `ms-transportistas`

Integracion protegida: `ms-transportistas -> ms-users`.

- Circuit Breaker: `userService`
- Retry: `userServiceRetry`
- Fallback controlado con `ExternalServiceException`
- Respuesta HTTP final ante caida de dependencia: `503 Service Unavailable`

### `ms-servicios`

Integraciones protegidas:

- `createShipment`: `shipmentService` + `shipmentServiceRetry`
- validacion de cliente (`ms-clientes`): `clientValidationService` + `clientValidationRetry`
- validacion de transporte (`ms-transportistas`): `transportValidationService` + `transportValidationRetry`

Fallbacks controlados con `ExternalServiceException` y respuesta HTTP `503` desde `GlobalExceptionHandler`.

## 5) Observabilidad

- Correlation ID propagado con header `X-Correlation-Id`
- Logging con `correlationId` en MDC
- Endpoints actuator habilitados por servicio
- Metricas Prometheus en `/actuator/prometheus`
- Endpoints de resiliencia activos (donde aplica):
  - `/actuator/circuitbreakers`
  - `/actuator/circuitbreakerevents`
  - `/actuator/retries`
  - `/actuator/retryevents`

## 6) Mensajeria

Se usa RabbitMQ para publicar eventos de negocio, por ejemplo:

- eventos de clientes
- eventos de transportes
- eventos de envios (creacion y cambio de estado)

## 7) Base de Datos

- Cada microservicio usa su propia base de datos en PostgreSQL.
- Esquema gestionado con Flyway.
- Configuracion JPA principal con `ddl-auto=validate`.

## 8) Guia de Arranque Completa

### 8.1 Prerrequisitos

- Java 21 instalado
- Docker Desktop activo
- Puertos libres: `5432`, `5672`, `15672`, `8761`, `8080`, `8081`, `8082`, `8083`, `8084`, `8085`, `8086`

### 8.2 Levantar infraestructura

Desde la raiz del proyecto:

```bash
docker compose -f docker/docker-compose.yml up -d
```

Verificar contenedores:

```bash
docker ps
```

Deben aparecer:

- `postgres-transportes`
- `rabbitmq-simple`

### 8.3 Levantar microservicios

#### Opcion A (Git Bash / Linux / Mac)

```bash
./scripts/start-microservices.sh
```

#### Opcion B (PowerShell en Windows)

Levantar en este orden, cada uno en terminal separada:

1. `eureka-server`

```powershell
cd eureka-server
.\mvnw.cmd -Dmaven.test.skip=true spring-boot:run
```

2. `ms-users`

```powershell
cd ms-users
.\mvnw.cmd -Dmaven.test.skip=true spring-boot:run
```

3. `ms-clientes`

```powershell
cd ms-clientes
.\mvnw.cmd -Dmaven.test.skip=true spring-boot:run
```

4. `ms-servicios`

```powershell
cd ms-servicios
.\mvnw.cmd -Dmaven.test.skip=true spring-boot:run
```

5. `ms-transportistas`

```powershell
cd ms-transportistas
.\mvnw.cmd -Dmaven.test.skip=true spring-boot:run
```

6. `ms-auditoria`

```powershell
cd ms-auditoria
.\mvnw.cmd -Dmaven.test.skip=true spring-boot:run
```

7. `ms-notificaciones`

```powershell
cd ms-notificaciones
.\mvnw.cmd -Dmaven.test.skip=true spring-boot:run
```

8. `api-gateway`

```powershell
cd api-gateway
.\mvnw.cmd -Dmaven.test.skip=true spring-boot:run
```

## 9) Verificacion Rapida

- Eureka: `http://localhost:8761`
- Gateway: `http://localhost:8080`
- RabbitMQ UI: `http://localhost:15672`
  - usuario: `admin`
  - password: `admin`

Health checks:

- `http://localhost:8080/actuator/health`
- `http://localhost:8081/actuator/health`
- `http://localhost:8082/actuator/health`
- `http://localhost:8083/actuator/health`
- `http://localhost:8084/actuator/health` (puede responder `403` segun politica de seguridad de `ms-users`)
- `http://localhost:8085/actuator/health`
- `http://localhost:8086/actuator/health`

Resiliencia:

- `http://localhost:8082/actuator/circuitbreakers`
- `http://localhost:8082/actuator/retries`
- `http://localhost:8083/actuator/circuitbreakers`
- `http://localhost:8083/actuator/retries`

Auditoria:

- `GET http://localhost:8080/api/audit/events`
- `GET http://localhost:8080/api/audit/events/{auditId}`

Notificaciones:

- `GET http://localhost:8080/api/notifications?transportUserId={userId}`
- `GET http://localhost:8080/api/notifications/unread-count?transportUserId={userId}`
- `PATCH http://localhost:8080/api/notifications/{notificationId}/read?transportUserId={userId}`
- `GET http://localhost:8080/api/notifications/stream?transportUserId={userId}` (SSE tiempo real para transportista)
- `transportUserId` es obligatorio en todos los endpoints de notificaciones.
- Eventos visibles para transportista:
  - `transport.assigned`
  - `transport.status.changed`
  - `services.shipment.created`
  - `services.shipment.status.updated`

## 10) Uso Desde Frontend (ms-auditoria)

Flujo recomendado de prueba en UI:

1. Ejecutar una accion de negocio en frontend (crear cliente, crear envio, crear/asignar/cambiar estado de transporte).
2. Consultar auditoria por gateway:
   - `GET /api/audit/events?size=20`
3. Filtrar por parametros segun necesidad:
   - `source` (`ms-clientes`, `ms-servicios`, `ms-transportistas`)
   - `eventType`
   - `routingKey`
   - `correlationId`
   - `from` y `to` (ISO-8601)
4. Ver detalle de evento:
   - `GET /api/audit/events/{auditId}`

Ejemplo rapido:

- `GET http://localhost:8080/api/audit/events?source=ms-clientes&size=10`

Si el frontend recibe `404` en `/api/audit/events`, reiniciar `api-gateway` para recargar rutas.
## 11) Login Inicial (Administrador)

`ms-users` crea automaticamente un usuario admin si no existe:

- email: `admin@transportes.local`
- password: `Admin123!`
- role: `ADMIN`

Ejemplo:

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "userEmail": "admin@transportes.local",
  "userPassword": "Admin123!"
}
```

## 12) Apagar Todo

### Opcion A (Git Bash / Linux / Mac)

```bash
./scripts/stop-microservices.sh
```

### Opcion B (manual)

Detener procesos Java de los servicios y luego bajar infraestructura:

```bash
docker compose -f docker/docker-compose.yml down
```

## 13) Logs

Los logs quedan en:

- `logs/`

## 14) Checklist Produccion

Antes de desplegar en prod, configurar variables de entorno y secretos:

- `EUREKA_URL`
- `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD`
- `MS_USERS_DB_URL`, `MS_USERS_DB_USERNAME`, `MS_USERS_DB_PASSWORD`
- `MS_CLIENTES_DB_URL`, `MS_CLIENTES_DB_USERNAME`, `MS_CLIENTES_DB_PASSWORD`
- `MS_SERVICIOS_DB_URL`, `MS_SERVICIOS_DB_USERNAME`, `MS_SERVICIOS_DB_PASSWORD`
- `MS_TRANSPORTISTAS_DB_URL`, `MS_TRANSPORTISTAS_DB_USERNAME`, `MS_TRANSPORTISTAS_DB_PASSWORD`
- `MS_AUDITORIA_DB_URL`, `MS_AUDITORIA_DB_USERNAME`, `MS_AUDITORIA_DB_PASSWORD`
- `MS_NOTIFICACIONES_DB_URL`, `MS_NOTIFICACIONES_DB_USERNAME`, `MS_NOTIFICACIONES_DB_PASSWORD`
- `MS_USERS_JWT_SECRET` (obligatorio en prod; no usar valor por defecto)
- `GATEWAY_ALLOWED_ORIGIN` (CORS en `api-gateway` perfil `prod`)

Recomendaciones de salida a prod:

- No usar credenciales por defecto (`postgres/1234`, `admin/admin`) fuera de desarrollo.
- Mantener `ddl-auto=validate` y migraciones Flyway habilitadas.
- Exponer solo endpoints de actuator necesarios por entorno.
- Verificar que el frontend siempre envie `transportUserId` en consultas SSE/REST de notificaciones.
