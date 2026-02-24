# Analisis de Microservicios - Proyecto Transportes

## 1) Resumen Ejecutivo
El repositorio implementa una arquitectura de microservicios con Spring Cloud y enfoque Hexagonal (Ports & Adapters):
- Service discovery con Eureka (`eureka-server`)
- API Gateway (`api-gateway`)
- Servicios de dominio: `ms-users`, `ms-clientes`, `ms-servicios`, `ms-transportistas`
- Mensajeria asincrona con RabbitMQ
- Persistencia por microservicio en PostgreSQL

En esta actualizacion tambien se registran mejoras ya implementadas en seguridad y configuracion.

## 2) Arquitectura del Repositorio
- `eureka-server`: registro y descubrimiento
- `api-gateway`: entrada unica y enrutamiento
- `ms-users`: autenticacion JWT y gestion de usuarios
- `ms-clientes`: gestion de clientes + eventos RabbitMQ
- `ms-servicios`: gestion de envios + validaciones cruzadas + eventos RabbitMQ
- `ms-transportistas`: gestion de transportes + eventos RabbitMQ + integracion con `ms-users`
- `docker/docker-compose.yml`: infraestructura local (PostgreSQL + RabbitMQ)

## 3) Comunicacion Entre Servicios
### 3.1 Sincrona
- Cliente -> `api-gateway` (`:8080`) -> microservicios por `lb://service-name` via Eureka
- `ms-servicios` -> `ms-transportistas` mediante OpenFeign para validar transporte
- `ms-transportistas` -> `ms-users` mediante OpenFeign

### 3.2 Asincrona
- RabbitMQ (`:5672`) con exchanges y routing keys por dominio:
  - clientes
  - envios
  - transportes

## 4) Seguridad
- `ms-users` gestiona login y JWT.
- Endpoints publicos:
  - `/api/auth/**`
  - `/api/users/register`
- `/api/users/**` restringido a rol `ADMIN`.
- `ms-transportistas` propaga el header `Authorization` en llamadas Feign.

## 5) Estado de Configuracion (Actualizado)
Puertos esperados:
- `eureka-server`: `8761`
- `api-gateway`: `8080`
- `ms-users`: `8084`
- `ms-clientes`: `8081`
- `ms-servicios`: `8082`
- `ms-transportistas`: `8083`

Observacion:
- Ya no hay conflicto base de puertos entre `ms-clientes` y `ms-servicios`.

## 6) Mejoras Implementadas
### 6.1 Seguridad en registro publico de usuarios
Antes:
- `POST /api/users/register` forzaba `ADMIN`.

Ahora:
- `POST /api/users/register` fuerza `CLIENT`.
- Se evita auto-asignacion de privilegios administrativos desde registro publico.

### 6.2 Correccion de naming en Gateway
Antes:
- ID de ruta mal escrito: `shioments-service`.

Ahora:
- ID corregido a `shipments-service`.

### 6.3 Externalizacion de configuracion sensible y operativa
Se habilito uso de variables de entorno (con defaults) para:
- URLs/credenciales de base de datos
- RabbitMQ host/port/credenciales
- URL de Eureka
- Puertos de los microservicios
- Secretos/seed admin de `ms-users` (JWT y admin inicial)

Servicios actualizados:
- `ms-users`
- `ms-clientes`
- `ms-servicios`
- `ms-transportistas`
- `api-gateway`
- `eureka-server`

## 7) Validacion de Regla de Negocio: Envio vs Estado de Transporte
Regla vigente en `ms-servicios`:
- Solo se permite crear envio cuando el transporte esta en estado `AVAILABLE`.
- Si el transporte esta en `MAINTENANCE`, `IN_TRANSIT` o `OUT_OF_SERVICE`, la creacion se bloquea con `400 Bad Request`.

## 8) Riesgos Pendientes (Backlog Tecnico)
1. Versionado heterogeneo de Spring Boot/Spring Cloud entre servicios.
2. Observabilidad transversal incompleta (tracing/correlation-id estandarizado).
3. Criterio mixto Web/WebFlux; conviene estandarizar por servicio.
4. Definir estrategia formal de secretos para produccion (Vault/KMS/secret manager) en lugar de defaults locales.

## 9) Propuesta de Implementacion (Siguiente Iteracion)
1. Unificar versiones parent/BOM en todos los `pom.xml`.
2. Incorporar `SPRING_PROFILES_ACTIVE` y perfil `prod` sin credenciales por defecto.
3. Agregar `correlation-id` en gateway + propagacion entre servicios.
4. Añadir tests de seguridad para:
   - Registro publico sin privilegios
   - Restriccion de `/api/users/**` solo ADMIN
5. Añadir tests de integracion para la regla de creacion de envio por estado del transporte.
