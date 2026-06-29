@echo off
REM ====================================================================
REM  Lynx - Arranca la infraestructura en Docker (modo hibrido)
REM  Postgres (pgvector) + Redis + Kafka + Zookeeper + voice-service
REM  (c) 2026 Abel Gomez. Todos los derechos reservados.
REM ====================================================================
cd /d "%~dp0.."
echo Levantando infraestructura de Lynx en Docker...
docker compose -f docker-compose.infra.yml up -d
echo.
echo Esperando a que los servicios esten listos...
timeout /t 8 /nobreak >nul
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | findstr lynx
echo.
echo Infraestructura lista:
echo   - PostgreSQL (pgvector)  host 5433
echo   - Redis                  6379  (clave: redis_lynx_2024)
echo   - Kafka                  9092
echo   - voice-service          8083
pause