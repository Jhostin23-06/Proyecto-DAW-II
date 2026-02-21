#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOG_DIR="$ROOT_DIR/logs"

SERVICES=(
  "api-gateway"
  "ms-transportistas"
  "ms-servicios"
  "ms-clientes"
  "ms-users"
  "eureka-server"
)

stop_service() {
  local service_name="$1"
  local pid_file="$LOG_DIR/$service_name.pid"

  if [[ ! -f "$pid_file" ]]; then
    echo "[skip] No existe PID para $service_name ($pid_file)"
    return 0
  fi

  local pid
  pid="$(cat "$pid_file" 2>/dev/null || true)"

  if [[ -z "$pid" ]]; then
    echo "[warn] PID vacio para $service_name"
    rm -f "$pid_file"
    return 0
  fi

  if kill -0 "$pid" >/dev/null 2>&1; then
    echo "[stop] Deteniendo $service_name (PID $pid)..."
    kill "$pid" >/dev/null 2>&1 || true

    local waited=0
    while kill -0 "$pid" >/dev/null 2>&1; do
      if (( waited >= 15 )); then
        echo "[force] Forzando cierre de $service_name (PID $pid)"
        kill -9 "$pid" >/dev/null 2>&1 || true
        break
      fi
      sleep 1
      waited=$((waited + 1))
    done

    echo "[ok] $service_name detenido"
  else
    echo "[skip] $service_name no estaba corriendo (PID $pid)"
  fi

  rm -f "$pid_file"
}

stop_rabbitmq() {
  if command -v docker >/dev/null 2>&1; then
    if [[ -f "$ROOT_DIR/docker/docker-compose.yml" ]]; then
      echo "[infra] Bajando infraestructura (PostgreSQL + RabbitMQ)..."
      (
        cd "$ROOT_DIR/docker"
        docker compose down
      ) || echo "[warn] No se pudo bajar RabbitMQ con docker compose"
    else
      echo "[skip] No existe $ROOT_DIR/docker/docker-compose.yml"
    fi
  else
    echo "[skip] Docker no disponible, se omite apagado de RabbitMQ"
  fi
}

echo "== Apagado ordenado de microservicios =="

for service in "${SERVICES[@]}"; do
  stop_service "$service"
done

stop_rabbitmq

echo "[done] Proceso de apagado finalizado"
