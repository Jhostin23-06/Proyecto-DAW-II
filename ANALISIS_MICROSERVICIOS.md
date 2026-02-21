# Analisis de Microservicios - Proyecto Transportes

## 1) Resumen Ejecutivo
El repositorio implementa una arquitectura de microservicios con Spring Cloud:
- Service discovery con Eureka (`eureka-server`).
- API Gateway (`api-gateway`) para enrutamiento centralizado.
- Servicios de dominio: `ms-users`, `ms-clientes`, `ms-servicios`, `ms-transportistas`.
- Mensajeria asincrona con RabbitMQ (eventos de clientes, envios y transportes).
- Persistencia separada por microservicio en PostgreSQL (database-per-service).

Tambien hay una organizacion de codigo por capas tipo Hexagonal/Clean en los servicios de dominio (`application`, `domain`, `infrastructure`).

## 2) Estructura General del Repositorio
- `eureka-server`: registro y descubrimiento de servicios.
- `api-gateway`: entrada unica para rutas REST.
- `ms-users`: autenticacion/autorizacion JWT y gestion de usuarios.
- `ms-clientes`: gestion de clientes + publicacion de eventos RabbitMQ.
- `ms-servicios`: gestion de envios/shipments + publicacion de eventos RabbitMQ.
- `ms-transportistas`: gestion de transportes + integracion Feign contra `ms-users` + eventos RabbitMQ.
- `docker/docker-compose.yml`: levanta RabbitMQ con panel de administracion.

## 3) Arquitectura Identificada
### 3.1 Patron de arquitectura por servicio
En `ms-users`, `ms-clientes`, `ms-servicios` y `ms-transportistas` se observa:
- `domain`: modelos de negocio, reglas y contratos de dominio.
- `application`: casos de uso y puertos (in/out).
- `infrastructure`: controladores REST, persistencia, mensajeria, configuracion.

Esto corresponde a una variante de Arquitectura Hexagonal (Ports & Adapters).

### 3.2 Comunicacion entre servicios
- Sincrona:
  - Cliente -> `api-gateway` (`:8080`) -> microservicios por `lb://service-name` via Eureka.
  - `ms-transportistas` -> `ms-users` mediante OpenFeign (`@FeignClient(name = "ms-users")`).
- Asincrona:
  - RabbitMQ (`:5672`) con exchanges/queues por contexto de negocio.

### 3.3 Seguridad
- `ms-users` implementa JWT (`jjwt`) + Spring Security.
- Endpoints publicos: `/api/auth/**`, `/api/users/register`.
- `/api/users/**` requiere rol ADMIN.
- `ms-transportistas` propaga header `Authorization` hacia llamadas Feign (token forwarding).

## 4) Componentes y Configuracion Tecnica
## 4.1 Service Discovery
### `eureka-server`
- Puerto: `8761`.
- No se registra a si mismo (`register-with-eureka=false`).

## 4.2 API Gateway
### `api-gateway`
- Puerto: `8080`.
- Rutas declaradas:
  - `/api/transports/**` -> `ms-transportistas`
  - `/api/users/**` -> `ms-users`
  - `/api/auth/**` -> `ms-users`
  - `/api/clients/**` -> `ms-clientes`
  - `/api/shipments/**` -> `ms-servicios`
- CORS habilitado para frontend local en `http://localhost:4200` y `http://127.0.0.1:4200`.

## 4.3 Microservicios de Dominio
### `ms-users`
- Puerto: `8084`.
- DB: `jdbc:postgresql://localhost:5432/dbusers`.
- Stack: Web, JPA, Validation, Security, JWT, Eureka.
- Endpoints principales:
  - `POST /api/auth/login`
  - `POST /api/users/register`
  - CRUD en `/api/users`.

### `ms-clientes`
- Puerto: `8081`.
- DB: `jdbc:postgresql://localhost:5432/clientes_db`.
- Stack: Web, WebFlux, JPA, Validation, AMQP, Eureka.
- Endpoints principales:
  - `GET /api/clients`
  - `POST /api/clients`
  - `PATCH /api/clients/{id}`
  - `DELETE /api/clients/{id}`
- Eventos RabbitMQ:
  - Exchange: `clients.exchange`
  - Routing key: `clients.created`
  - Queue: `clients.created.queue`

### `ms-servicios`
- Puerto configurado: `8081` (conflicto con `ms-clientes`).
- DB: `jdbc:postgresql://localhost:5432/dbservicios`.
- Stack: Web, WebFlux, JPA, Validation, AMQP, Eureka, Actuator, Resilience4j.
- Endpoints principales:
  - `POST /api/shipments`
  - `GET /api/shipments/{id}`
  - `GET /api/shipments`
- Eventos RabbitMQ:
  - Exchange: `services.exchange`
  - Routing key: `services.shipment.created`
  - Queue: `services.shipment.created.queue`

### `ms-transportistas`
- Puerto: `8083`.
- DB: `jdbc:postgresql://localhost:5432/dbtransportista`.
- Stack: Web, WebFlux, JPA, Validation, AMQP, OpenFeign, Eureka.
- Endpoints principales:
  - `POST /api/transports`
  - `GET /api/transports/{id}`
  - `GET /api/transports`
  - `GET /api/transports/available/search`
  - `PUT /api/transports/{id}/status`
  - `PATCH /api/transports/{id}/location`
  - `PATCH /api/transports/{id}`
  - `PATCH /api/transports/{id}/assign`
  - `DELETE /api/transports/{id}`
- Eventos RabbitMQ:
  - Exchange: `transport.exchange`
  - Routing keys: `transport.created`, `transport.status.changed`, `transport.assigned`
  - Queues: `transport.created.queue`, `transport.status.changed.queue`, `transport.assigned.queue`

## 5) Orden de Arranque Recomendado
1. RabbitMQ (y PostgreSQL si aplica).
2. `eureka-server`.
3. `ms-users` (dependencia de autenticacion y Feign para transportistas).
4. `ms-clientes`.
5. `ms-servicios`.
6. `ms-transportistas`.
7. `api-gateway`.

## 6) Hallazgos y Riesgos Detectados
1. Conflicto de puerto:
- `ms-clientes` y `ms-servicios` estan en `8081`.
- No pueden ejecutarse simultaneamente sin override de puerto.

2. Versionado heterogeneo:
- Algunos servicios usan Spring Boot `4.0.2` y otros `3.5.10`.
- Tambien hay distintas versiones de Spring Cloud (`2025.0.0` y `2025.1.0`).
- Riesgo de incompatibilidad entre dependencias en tiempo de ejecucion.

3. Regla de registro de usuarios potencialmente incorrecta:
- En `UserController`, endpoint `/api/users/register` asigna `ADMIN` explicitamente, aunque el comentario dice evitar auto-asignacion de ADMIN.
- Riesgo de seguridad/privilegios.

4. Credenciales hardcodeadas en `application.yml/properties`:
- PostgreSQL (`postgres/1234`, `postgres/postgres`) y RabbitMQ (`admin/admin`).
- Conviene externalizar via variables de entorno o vault.

5. Posible inconsistencia de naming en ruta Gateway:
- ID de ruta `shioments-service` (typo en nombre de ID).
- No rompe funcionalidad directamente, pero afecta mantenimiento/observabilidad.

6. Puerto de servicios no centralizado:
- Falta estandarizar puertos por microservicio en un catalogo unico de configuracion.

7. Puerto de evento no implementado en users:
- Existe `UserEventPort` sin adapter concreto visible.
- Puede ser deuda tecnica o parte incompleta del flujo event-driven.

## 7) Script de Arranque Incluido
Se agrega `scripts/start-microservices.sh` para:
- Levantar RabbitMQ via `docker/docker-compose.yml`.
- Iniciar servicios en orden.
- Esperar disponibilidad de puertos antes de seguir.
- Guardar logs en `./logs`.
- Evitar conflicto levantando `ms-servicios` en `8082` por override temporal.

## 8) Recomendaciones de Mejora
1. Estandarizar versiones de Spring Boot y Spring Cloud para todo el ecosistema.
2. Resolver definitivamente el conflicto de puertos en configuracion base.
3. Externalizar secretos a variables de entorno/perfiles.
4. Corregir la logica de rol por defecto en registro de usuarios.
5. Incorporar observabilidad transversal (tracing, correlation-id, health checks homogeneos).
6. Unificar criterio de framework web (Web o WebFlux) por servicio para reducir complejidad.
