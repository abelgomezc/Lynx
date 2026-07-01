# ▶️ Lynx · Cómo LEVANTAR todo (modo híbrido)

> **© 2026 Abel Gomez. Todos los derechos reservados.**
>
> **Modo híbrido** = infraestructura + voice-service en **Docker**, y los
> 6 servicios **Java** + el **frontend** en **local** (Maven / npm).
> Es el modo recomendado en este equipo porque tu PostgreSQL local no
> trae pgvector y no tienes ffmpeg instalado.

---

## 🗺️ Mapa de lo que corre y dónde

| Componente | Dónde | Puerto |
|------------|-------|--------|
| PostgreSQL (pgvector) | Docker | host **5433** → 5432 |
| Redis | Docker | 6379 (clave `redis_lynx_2024`) |
| Kafka + Zookeeper | Docker | 9092 / 2181 |
| voice-service (Whisper) | Docker | 8083 |
| eureka-server | Local | 8761 |
| api-gateway | Local | 8080 |
| auth-service | Local | 8081 |
| face-service | Local | 8082 |
| access-service | Local | 8084 |
| notification-service | Local | 8085 |
| lynx-ui (frontend) | Local | 5173 |

> ⚠️ El Postgres de Docker está en el host en **5433** (para no chocar con
> tu PostgreSQL local en 5432). Por eso los servicios Java se arrancan con
> `SPRING_DATASOURCE_URL=...localhost:5433/...` (los comandos de abajo ya
> lo incluyen).

---

## ⚡ Atajo: un solo comando

Con Docker Desktop abierto y los JAR ya compilados:

```bash
bash scripts/levantar.sh
```

Arranca la infra Docker + los 6 servicios Java + el frontend, y deja los logs
en `logs/`. Para detener todo, usa [DETENER.md](DETENER.md). El resto de esta
guía explica el arranque paso a paso.

---

## 1️⃣ Levantar la infraestructura (Docker)

Con Docker Desktop abierto:

```bash
docker compose -f docker-compose.infra.yml up -d
```

Comprueba que estén arriba:

```bash
docker ps --format "table {{.Names}}\t{{.Status}}"
```

Debes ver `lynx-postgres` (healthy), `lynx-redis`, `lynx-kafka`,
`lynx-zookeeper` y `lynx-voice`.

> La primera vez tarda (descarga imágenes y construye el voice-service con
> Whisper). El voice-service responde en http://localhost:8083/health

---

## 2️⃣ Compilar los servicios Java (una vez, o tras cambios)

> En esta red hay intercepción SSL, así que Maven necesita el transporte
> `wagon` con SSL inseguro:

```bash
mvn clean install -Dmaven.test.skip=true \
  -Dmaven.resolver.transport=wagon \
  -Dmaven.wagon.http.ssl.insecure=true \
  -Dmaven.wagon.http.ssl.allowall=true
```

---

## 3️⃣ Arrancar los servicios Java (cada uno en su terminal)

> Orden: **primero Eureka**, espera ~25 s, luego el resto.
> Usa los JAR ya compilados (`*/target/*.jar`).

```bash
# 1) Eureka  (espera a que diga "Started" antes de seguir)
java -jar eureka-server/target/eureka-server-1.0.0.jar

# 2) Gateway
java -jar api-gateway/target/api-gateway-1.0.0.jar

# 3) Auth   (apunta a la BD en 5433)
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/lynx_auth \
  java -jar auth-service/target/auth-service-1.0.0.jar

# 4) Face
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/lynx_biometria \
  java -jar face-service/target/face-service-1.0.0.jar

# 5) Access
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/lynx_accesos \
  java -jar access-service/target/access-service-1.0.0.jar

# 6) Notification
java -jar notification-service/target/notification-service-1.0.0.jar
```

> 💡 En **PowerShell** la variable se pasa distinto:
> ```powershell
> $env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5433/lynx_auth"
> java -jar auth-service/target/auth-service-1.0.0.jar
> ```
>
> 💡 Alternativa: `cd auth-service && mvn spring-boot:run` (más lento por
> arranque de Maven; usa los mismos `SPRING_DATASOURCE_URL`).

---

## 4️⃣ Arrancar el frontend

```bash
cd lynx-ui
npm install --strict-ssl=false   # solo la primera vez
npm run dev
```

Abre 👉 **http://localhost:5173**

---

## 5️⃣ Comprobar que todo está vivo

```bash
# Health de cada servicio
curl http://localhost:8761/actuator/health   # eureka
curl http://localhost:8080/actuator/health   # gateway
curl http://localhost:8081/actuator/health   # auth
curl http://localhost:8082/actuator/health   # face
curl http://localhost:8084/actuator/health   # access
curl http://localhost:8085/actuator/health   # notification
curl http://localhost:8083/health            # voice (docker)
```

Panel de Eureka (deben aparecer los 5 servicios + gateway):
👉 **http://localhost:8761**

---

## ⚠️ Para que funcione el reconocimiento facial

Descarga los modelos de face-api.js y cópialos en
`lynx-ui/public/models/face_api_models/` (ver el README de esa carpeta).
Sin ellos la cámara enciende pero no genera el embedding.

---

## 🧪 Datos sembrados

- Admin inicial: **admin@lynx.com** (rol ADMIN) — debe registrar su biometría.

---

Para apagar todo, mira 👉 [DETENER.md](DETENER.md)

**© 2026 Abel Gomez. Todos los derechos reservados.**