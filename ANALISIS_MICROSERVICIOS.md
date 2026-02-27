# Analisis de Microservicios - Proyecto Transportes

## 1) Objetivo y alcance
Este documento reemplaza el analisis previo con una revision tecnica del repositorio archivo por archivo (archivos funcionales), su estructura actual y un plan de mejora priorizado.

Fecha de corte del analisis: 2026-02-26.

Cobertura revisada:
- `api-gateway`: 3 archivos (`main/resources/test`)
- `eureka-server`: 3 archivos (`main/resources/test`)
- `ms-users`: 39 archivos
- `ms-clientes`: 30 archivos
- `ms-servicios`: 63 archivos
- `ms-transportistas`: 40 archivos
- Infra/scripts/root: `README.md`, `docker/*`, `scripts/*`, `pom.xml` por modulo

No se incluyeron wrappers `mvnw*` ni logs en detalle funcional.

## 2) Evidencia tecnica (build y tests)
Comandos ejecutados y resultado:

1. `ms-clientes -> ./mvnw -q test`
- Resultado: OK.
- Observacion: smoke test de contexto con DB/Eureka reales.

2. `ms-users -> ./mvnw -q test`
- Resultado: OK.
- Observacion: smoke test de contexto con DB/Eureka reales.

3. `ms-transportistas -> ./mvnw -q test`
- Resultado: OK.
- Observacion: test de contexto levanta DB/Eureka reales (no aislado).

4. `ms-servicios -> ./mvnw -q test`
- Resultado: OK.
- Observacion: test de contexto levanta DB/Eureka reales (no aislado).

5. `api-gateway -> ./mvnw -q test`
- Resultado: OK.
- Observacion: persisten warnings de deprecacion de propiedades de gateway para una futura migracion.

6. `eureka-server -> ./mvnw -q test`
- Resultado: OK.
- Observacion: warning por proveedor de validacion ausente en classpath (impacto bajo en este modulo).

Estado global al cierre de esta actualizacion (2026-02-26): `6/6` modulos en verde con `mvn test`.
## 3) Estructura actual del repositorio
- `api-gateway`: ruteo y CORS de entrada.
- `eureka-server`: discovery server.
- `ms-users`: autenticacion JWT y gestion de usuarios.
- `ms-clientes`: CRUD de clientes + evento RabbitMQ.
- `ms-servicios`: CRUD de envios/catalogos + validaciones con Feign + eventos RabbitMQ.
- `ms-transportistas`: CRUD/estado de transportes + eventos RabbitMQ + consumo de `ms-users`.
- `docker`: PostgreSQL + RabbitMQ local.
- `scripts`: arranque/parada local.
- `logs`: salida de ejecucion local (actualmente versionada/modificada en worktree).

## 4) Revision archivo por archivo (mejoras)

### 4.1 Raiz, Docker y Scripts
- `README.md`
  - Estado: guia clara para levantar entorno local.
  - Mejora: agregar seccion de perfiles (`dev/test/prod`) y troubleshooting de tests aislados.
- `docker/docker-compose.yml`
  - Estado: infraestructura minima funcional.
  - Mejora: mover credenciales a `.env` y agregar politica de reinicio/healthcheck tambien para RabbitMQ.
- `docker/postgres/init/01-create-microservices-databases.sql`
  - Estado: correcto para bootstrap inicial.
  - Mejora: versionar migraciones reales con Flyway/Liquibase por microservicio.
- `scripts/start-microservices.sh`
  - Estado: orden de arranque correcto.
  - Mejora: comentario de conflicto de puertos esta desactualizado; extraer puertos a variables unicas.
- `scripts/start-microservices-mac.sh`
  - Estado: mejor robustez que script bash base.
  - Mejora: mantener una sola version y parametrizar comportamiento por OS.
- `scripts/stop-microservices.sh`
  - Estado: funcional.
  - Mejora: hacer `docker compose down` opcional (flag) para no apagar infraestructura siempre.

### 4.2 api-gateway
- `api-gateway/pom.xml`
  - Hallazgo: usa starter de gateway deprecado.
  - Mejora: migrar a `spring-cloud-starter-gateway-server-webflux`.
- `api-gateway/src/main/resources/application.yml`
  - Hallazgo: keys de gateway en esquema viejo; warnings de migracion en test.
  - Mejora: mover a `spring.cloud.gateway.server.webflux.*`; bajar logs DEBUG para entorno no local.
- `api-gateway/src/main/java/com/core/apigateway/ApiGatewayApplication.java`
  - Estado: correcto/minimo.
  - Mejora: sin urgencia.
- `api-gateway/src/test/java/com/core/apigateway/ApiGatewayApplicationTests.java`
  - Estado: solo smoke test.
  - Mejora: agregar tests de rutas (WebTestClient) y CORS.

### 4.3 eureka-server
- `eureka-server/pom.xml`
  - Hallazgo: usa Boot 4.0.2 mientras hay modulos en 3.5.10.
  - Mejora: unificar BOM/parent en todos los servicios.
- `eureka-server/src/main/resources/application.properties`
  - Estado: correcto para dev.
  - Mejora: agregar `management` y perfil `prod`.
- `eureka-server/src/main/java/com/core/eurekaserver/EurekaServerApplication.java`
  - Estado: correcto.
  - Mejora: sin urgencia.
- `eureka-server/src/test/java/com/core/eurekaserver/EurekaServerApplicationTests.java`
  - Estado: smoke test.
  - Mejora: ejecutar con perfil `test` sin levantar server completo cuando no sea necesario.

### 4.4 ms-users
- `ms-users/pom.xml`
  - Hallazgo critico: no tiene dependencia de tests (`spring-boot-starter-test`).
  - Mejora: agregar dependencia y alinear versiones Cloud/Boot con resto.
- `ms-users/src/main/resources/application.yml`
  - Hallazgo: `ddl-auto=update`, `show-sql=true`, defaults sensibles en JWT/admin.
  - Mejora: separar perfiles y eliminar secretos por defecto en `prod`.
- `ms-users/src/main/java/com/core/msusers/infrastructure/controller/UserController.java`
  - Hallazgo: responde `UserResponse` completo (incluye password hash).
  - Mejora: DTO de salida sin `userPassword`.
- `ms-users/src/main/java/com/core/msusers/infrastructure/controller/AuthController.java`
  - Estado: correcto.
  - Mejora: agregar rate limit/bloqueo por intentos.
- `ms-users/src/main/java/com/core/msusers/infrastructure/security/SecurityConfig.java`
  - Hallazgo: matchers duplicados en auth; politicas basicas.
  - Mejora: limpiar reglas y agregar politica explicita para actuators/docs.
- `ms-users/src/main/java/com/core/msusers/infrastructure/security/JwtTokenProvider.java`
  - Hallazgo: valor default de secret en codigo/config.
  - Mejora: fallar al arrancar si secret no esta definido en entorno no dev.
- `ms-users/src/main/java/com/core/msusers/infrastructure/security/JwtAuthenticationFilter.java`
  - Estado: funcional.
  - Mejora: reducir logging sensible y centralizar manejo de auth errors.
- `ms-users/src/main/java/com/core/msusers/infrastructure/persistence/adapter/UserPersistenceAdapter.java`
  - Hallazgos: `findByEmail` retorna `null`; metodo `findByEmailAndPassword` obsoleto retorna vacio.
  - Mejora: usar `Optional` y remover codigo muerto.
- `ms-users/src/main/java/com/core/msusers/domain/bean/UserResponse.java`
  - Hallazgo critico: expone `userPassword`.
  - Mejora: eliminar campo de respuesta publica.
- `ms-users/src/main/java/com/core/msusers/infrastructure/configuration/AdminUserInitializer.java`
  - Hallazgo: puede promover a ADMIN por email configurado.
  - Mejora: proteger con flag de entorno y auditoria.
- `ms-users/src/main/java/com/core/msusers/application/port/outservice/UserPersistencePort.java`
  - Hallazgo: interfaz anotada con `@Component` (anti-patron).
  - Mejora: quitar anotacion de puertos/interfaces.
- `ms-users/src/test/java/com/core/msusers/MsUsersApplicationTests.java`
  - Hallazgo: hoy no compila por dependencias faltantes.
  - Mejora: habilitar build de tests y crear tests de seguridad.

### 4.5 ms-clientes
- `ms-clientes/pom.xml`
  - Hallazgo: mezcla `web` + `webflux`.
  - Mejora: elegir stack unico (recomendado `spring-boot-starter-web` para este servicio).
- `ms-clientes/src/main/resources/application.properties`
  - Hallazgo: archivo redundante (solo `spring.application.name`).
  - Mejora: consolidar en un solo archivo config.
- `ms-clientes/src/main/resources/application.yaml`
  - Hallazgo: `ddl-auto=update`, `show-sql=true` en default.
  - Mejora: mover settings de schema/sql a perfil `dev`.
- `ms-clientes/compose.yaml`
  - Hallazgo: compose local aislado y con credenciales hardcodeadas.
  - Mejora: eliminar o unificar con `docker/docker-compose.yml`.
- `ms-clientes/src/main/java/core/cibertec/ms_clientes/infrastructure/controller/ClientController.java`
  - Estado: endpoints claros.
  - Mejora: agregar paginacion y `@Valid` en patch cuando se definan constraints.
- `ms-clientes/src/main/java/core/cibertec/ms_clientes/domain/model/Client.java`
  - Estado: valida reglas clave.
  - Mejora: centralizar mensajes y normalizar texto antes de persistir.
- `ms-clientes/src/main/java/core/cibertec/ms_clientes/infrastructure/persistence/adapter/ClientPersistenceAdapter.java`
  - Estado: correcto.
  - Mejora: paginacion/filtros con `Pageable` y ordenar por defecto.
- `ms-clientes/src/main/java/core/cibertec/ms_clientes/infrastructure/controller/GlobalExceptionHandler.java`
  - Estado: consistente dentro del modulo.
  - Mejora: unificar formato de error con los otros microservicios.
- `ms-clientes/src/test/java/core/cibertec/ms_clientes/MsClientesApplicationTests.java`
  - Estado: smoke test valido.
  - Mejora: usar perfil test con DB embebida/Testcontainers.
- `ms-clientes/src/test/java/com/transporte/msclientes/MsClientesApplicationTests.java`
  - Hallazgo critico: test duplicado en paquete incorrecto rompe build.
  - Mejora: eliminarlo o moverlo al paquete correcto.

### 4.6 ms-servicios
- `ms-servicios/pom.xml`
  - Hallazgo: mezcla `web` + `webflux`; versions no alineadas con gateway/users.
  - Mejora: estandarizar stack y BOM de todo el repo.
- `ms-servicios/src/main/resources/application.yaml`
  - Hallazgo: `ddl-auto=update`, `show-sql=true` por defecto.
  - Mejora: usar perfiles y desactivar SQL verbose fuera de dev.
- `ms-servicios/src/main/java/core/cibertec/ms_servicios/application/service/CreateShipmentServiceImpl.java`
  - Hallazgo critico: fallback de circuit breaker devuelve respuesta parcial "PENDING" en vez de error tecnico.
  - Mejora: devolver 503/controlado y no simular exito.
- `ms-servicios/src/main/java/core/cibertec/ms_servicios/infrastructure/outservice/adapter/ClientValidationAdapter.java`
  - Hallazgo: en errores Feign retorna `false` indistinguible de "cliente no existe".
  - Mejora: separar error tecnico vs negocio.
- `ms-servicios/src/main/java/core/cibertec/ms_servicios/infrastructure/outservice/adapter/TransportValidationAdapter.java`
  - Hallazgo: mismo problema de ambiguedad en errores externos.
  - Mejora: propagar excepciones de integracion mapeadas a 503.
- `ms-servicios/src/main/java/core/cibertec/ms_servicios/infrastructure/persistence/adapter/ShipmentPersistenceAdapter.java`
  - Hallazgo: `catch(Exception)` + `RuntimeException` generica; `findById` y `updateStatus` retornan null.
  - Mejora: usar excepciones de dominio y eliminar retornos nulos.
- `ms-servicios/src/main/java/core/cibertec/ms_servicios/infrastructure/persistence/adapter/CategoryPersistenceAdapter.java`
  - Hallazgo: calcula IDs manualmente (riesgo de race condition).
  - Mejora: usar secuencia/identity autogenerada.
- `ms-servicios/src/main/java/core/cibertec/ms_servicios/infrastructure/persistence/adapter/StatusPersistenceAdapter.java`
  - Hallazgo: mismo problema de IDs manuales.
  - Mejora: autogeneracion DB + restriccion unica.
- `ms-servicios/src/main/java/core/cibertec/ms_servicios/infrastructure/controller/GlobalExceptionHandler.java`
  - Estado: base correcta.
  - Mejora: tipar errores de integracion (4xx vs 5xx) y agregar codigo interno.
- `ms-servicios/src/main/java/core/cibertec/ms_servicios/config/CategoryCatalogInitializer.java`
  - Estado: siembra inicial util.
  - Mejora: migrar a script de migracion versionado.
- `ms-servicios/src/main/java/core/cibertec/ms_servicios/config/StatusCatalogInitializer.java`
  - Estado: cubre backfill legacy.
  - Mejora: mover backfill a migracion unica, no en cada arranque.
- `ms-servicios/src/test/java/core/cibertec/ms_servicios/MsServiciosApplicationTests.java`
  - Estado: pasa.
  - Mejora: aislar de infraestructura real.

### 4.7 ms-transportistas
- `ms-transportistas/pom.xml`
  - Hallazgo: mezcla `web` + `webflux`.
  - Mejora: elegir stack unico.
- `ms-transportistas/src/main/resources/application.properties`
  - Hallazgo: defaults sensibles en DB/Rabbit.
  - Mejora: separar perfiles y secretos por entorno.
- `ms-transportistas/src/main/java/com/transporte/mstransportistas/infrastructure/configuration/TokenPropagationInterceptor.java`
  - Hallazgo critico: loguea token Authorization completo.
  - Mejora: eliminar log de token (o enmascarar).
- `ms-transportistas/src/main/java/com/transporte/mstransportistas/infrastructure/controller/TransportController.java`
  - Hallazgo: endpoint `/location` construye request no usado y envia `newStatus=null`.
  - Mejora: flujo dedicado para ubicacion sin pasar por transicion de estado.
- `ms-transportistas/src/main/java/com/transporte/mstransportistas/application/service/UpdateStatusServiceImpl.java`
  - Hallazgo: `TransportStatus.fromString(newStatusStr)` falla si `newStatusStr` es null.
  - Mejora: validar null y separar caso update-only-location.
- `ms-transportistas/src/main/java/com/transporte/mstransportistas/application/service/CreateTransportServiceImpl.java`
  - Hallazgo: validacion de usuario/rol comentada.
  - Mejora: reactivar validacion con manejo resiliente.
- `ms-transportistas/src/main/java/com/transporte/mstransportistas/infrastructure/persistence/adapter/TransportPersistenceAdapter.java`
  - Hallazgo: en `update` no actualiza `transportLicensePlate` aunque se valida en servicio.
  - Mejora: actualizar campo o quitar regla de validacion si no aplica.
- `ms-transportistas/src/main/java/com/transporte/mstransportistas/infrastructure/outservice/UserServiceAdapter.java`
  - Hallazgo: atrapa excepcion y retorna null silenciosamente.
  - Mejora: mapear errores de integracion y registrar contexto.
- `ms-transportistas/src/main/java/com/transporte/mstransportistas/infrastructure/messaging/adapter/TransportEventAdapter.java`
  - Estado: publica eventos.
  - Mejora: usar DTO de evento versionado, confirmar/retry y DLQ.
- `ms-transportistas/src/main/java/com/transporte/mstransportistas/application/port/*`
  - Hallazgo: interfaces anotadas con `@Component`.
  - Mejora: remover anotaciones en puertos.
- `ms-transportistas/src/test/java/com/transporte/mstransportistas/MsTransportistasApplicationTests.java`
  - Estado: pasa.
  - Mejora: aislar de DB/Eureka en perfil test.

## 5) Problemas prioritarios (orden de ataque)

### P0 (bloqueantes)
Estado: COMPLETADO.

### P1 (alta)
Estado: COMPLETADO en su alcance de estabilizacion de dominio/configuracion.

### P2 (media)
Estado: COMPLETADO para baseline operativo.
1. Eliminada mezcla `web` + `webflux` en microservicios de negocio.
2. Migrado a Flyway con `ddl-auto=validate` e inicializacion versionada.
3. Homologado contrato de error HTTP (`ErrorResponse`) entre microservicios.
4. Observabilidad base incorporada:
- `X-Correlation-Id` en gateway y microservicios (propagacion Feign incluida).
- Logs con `correlationId` en MDC.
- Actuator + Prometheus en modulos.

Pendiente para siguiente iteracion (P3/P4):
- Migracion de propiedades/starter de gateway a la nomenclatura nueva para eliminar warnings.
- Tests de integracion aislados (Testcontainers) para no depender de DB/Eureka locales.
## 6) Planning propuesto (4 iteraciones)

### Iteracion 1 (1-2 dias) - Estabilizacion
- Arreglar tests bloqueados (`ms-users`, `ms-clientes`).
- Corregir exposicion de password en respuestas de `ms-users`.
- Corregir log de token y bug de update de ubicacion en `ms-transportistas`.
- Entregable: build verde en todos los modulos con `mvn test`.

### Iteracion 2 (2-3 dias) - Compatibilidad y configuracion
- Unificar version Boot/Cloud en todos los `pom.xml`.
- Migrar `api-gateway` a nuevas keys/starter de Gateway.
- Crear perfiles `application-dev.yml`, `application-test.yml`, `application-prod.yml`.
- Entregable: arranque sin warnings criticos de deprecacion/config.

### Iteracion 3 (3-4 dias) - Calidad de dominio y errores
- Reemplazar retornos `null` por excepciones tipadas en adapters.
- Estandarizar `GlobalExceptionHandler` y contrato de error comun.
- Ajustar fallbacks de Resilience4j para no devolver exito falso.
- Entregable: errores de negocio/tecnicos diferenciados en API.

### Iteracion 4 (4-5 dias) - Plataforma y operacion
- Introducir migraciones DB (Flyway/Liquibase).
- Añadir tests de integracion aislados (Testcontainers).
- Agregar observabilidad minima: correlation-id + logs JSON + metrics basicas.
- Entregable: baseline de operacion y mantenimiento para produccion.

## 7) KPI sugeridos para medir mejora
- Build success rate por modulo: objetivo >= 95%.
- Tiempo de arranque en local completo: objetivo <= 3 min.
- Cobertura de tests de casos criticos: objetivo >= 60% en servicios de dominio.
- Incidencias por errores 5xx en flujos CRUD: reducir al menos 50%.

## 8) Resumen ejecutivo
El proyecto queda estable en su baseline tecnico tras cerrar P0, P1 y P2: build verde (`6/6` modulos), mayor consistencia de errores, trazabilidad transversal por `correlation-id`, y esquema de datos versionado con Flyway. La deuda remanente se desplaza a madurez operativa (tests de integracion aislados, limpieza de warnings de gateway y hardening adicional de seguridad/configuracion para produccion).