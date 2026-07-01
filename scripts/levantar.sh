#!/usr/bin/env bash
# =====================================================================
#  Lynx - Levantar entorno (modo híbrido) en UN comando
#  Infra en Docker (Postgres-pgvector:5433, Redis, Kafka, voice) +
#  6 servicios Java locales (JAR ya compilados) + frontend Vite.
#  © 2026 Abel Gomez. Todos los derechos reservados.
#
#  Uso:  bash scripts/levantar.sh
# =====================================================================
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT" || exit 1
DB="jdbc:postgresql://localhost:5433"
mkdir -p logs

echo "1/3 · Infraestructura Docker..."
docker compose -f docker-compose.infra.yml up -d

echo "2/3 · Eureka (espero a que registre)..."
nohup java -jar eureka-server/target/eureka-server-1.0.0.jar > logs/eureka.out 2>&1 &
until curl -s -o /dev/null http://localhost:8761/actuator/health; do sleep 2; done
echo "    Eureka OK"

echo "2/3 · Gateway + microservicios..."
nohup java -jar api-gateway/target/api-gateway-1.0.0.jar > logs/gateway.out 2>&1 &
SPRING_DATASOURCE_URL="$DB/lynx_auth"      nohup java -jar auth-service/target/auth-service-1.0.0.jar         > logs/auth.out 2>&1 &
SPRING_DATASOURCE_URL="$DB/lynx_biometria" nohup java -jar face-service/target/face-service-1.0.0.jar         > logs/face.out 2>&1 &
SPRING_DATASOURCE_URL="$DB/lynx_accesos"   nohup java -jar access-service/target/access-service-1.0.0.jar     > logs/access.out 2>&1 &
nohup java -jar notification-service/target/notification-service-1.0.0.jar > logs/notification.out 2>&1 &

echo "3/3 · Frontend (Vite)..."
( cd lynx-ui && nohup npm run dev > "$ROOT/logs/ui.out" 2>&1 & )

echo ""
echo "Arrancando... Frontend: http://localhost:5173  ·  Eureka: http://localhost:8761"
echo "Logs en: $ROOT/logs/"
