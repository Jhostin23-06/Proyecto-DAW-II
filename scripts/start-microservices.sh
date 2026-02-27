#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOG_DIR="$ROOT_DIR/logs"
MVN_COMMON_ARGS="-Dmaven.test.skip=true"
mkdir -p "$LOG_DIR"

wait_for_port() {
  local host="$1"
  local port="$2"
  local name="$3"
  local timeout="${4:-120}"

  echo "[wait] Esperando $name en $host:$port (timeout ${timeout}s)..."
  local start_ts
  start_ts=$(date +%s)

  while true; do
    if (echo > "/dev/tcp/$host/$port") >/dev/null 2>&1; then
      echo "[ok] $name disponible en $host:$port"
      return 0
    fi

    if (( $(date +%s) - start_ts >= timeout )); then
      echo "[error] Timeout esperando $name en $host:$port"
      return 1
    fi

    sleep 2
  done
}

start_service() {
  local service_name="$1"
  local service_dir="$2"
  local service_port="$3"
  local extra_args="${4:-}"

  echo "[start] Iniciando $service_name..."
  (
    cd "$ROOT_DIR/$service_dir"
    ./mvnw $MVN_COMMON_ARGS spring-boot:run $extra_args > "$LOG_DIR/$service_name.log" 2>&1
  ) &

  local pid=$!
  echo "$pid" > "$LOG_DIR/$service_name.pid"
  echo "[pid] $service_name -> $pid"

  wait_for_port "localhost" "$service_port" "$service_name"
}

start_rabbitmq() {
  if command -v docker >/dev/null 2>&1; then
    echo "[infra] Levantando infraestructura (PostgreSQL + RabbitMQ)..."
    (
      cd "$ROOT_DIR/docker"
      docker compose up -d
    )
    wait_for_port "localhost" "5432" "postgresql"
    wait_for_port "localhost" "5672" "rabbitmq"
    wait_for_port "localhost" "15672" "rabbitmq-management"
  else
    echo "[warn] Docker no encontrado. Se asume PostgreSQL y RabbitMQ ya estan corriendo localmente"
  fi
}

echo "== Inicio ordenado de microservicios =="
echo "Root: $ROOT_DIR"

start_rabbitmq

start_service "eureka-server" "eureka-server" "8761"
start_service "ms-users" "ms-users" "8084"
start_service "ms-clientes" "ms-clientes" "8081"

# Override para evitar conflicto con ms-clientes (ambos definen 8081 por defecto)
start_service "ms-servicios" "ms-servicios" "8082" "-Dspring-boot.run.arguments=--server.port=8082"

start_service "ms-transportistas" "ms-transportistas" "8083"
start_service "ms-auditoria" "ms-auditoria" "8085"
start_service "ms-notificaciones" "ms-notificaciones" "8086"
start_service "api-gateway" "api-gateway" "8080"

echo ""
echo "[done] Microservicios iniciados."
echo "[info] Logs en: $LOG_DIR"
echo "[info] Eureka: http://localhost:8761"
echo "[info] Gateway: http://localhost:8080"
