# 🐾 Lynx · Sistema de Autenticación Biométrica Dual: Cara + Voz

> **Sin contraseñas. Sin SMS. Sin apps externas.**
> Te registras una vez con tu cara y tu voz. Después, la cámara y el
> micrófono son tu identidad.
>
> **© 2026 Abel Gomez. Todos los derechos reservados.**

---

## ¿Qué es Lynx?

Lynx reemplaza por completo el usuario y la contraseña tradicionales con
**dos factores biométricos** en un solo flujo:

1. **Reconocimiento facial** con MediaPipe + face-api.js (embeddings de 128
   dimensiones generados en el navegador).
2. **Reconocimiento de voz** con Whisper local + MFCC (librosa) en Python.

Todo el procesamiento biométrico es **100% local**: ningún dato biométrico
se envía a la nube. Los vectores se guardan como embeddings en **pgvector**.

No es una biblioteca de autenticación: es una **plataforma completa** con
dashboard de usuario, panel de administración, historial de accesos en
tiempo real, alertas de seguridad y notificaciones por correo.

---

## Arquitectura

```
React (lynx-ui :5173)
   │ REST /api  +  WebSocket
   ▼
api-gateway :8080 ── valida JWT en cada petición
   │ lb:// (Eureka)
   ├──────────────► auth-service :8081 ──► face-service :8082 (pgvector)
   │                       │           └─► voice-service :8083 (Python/Whisper)
   │                       │ Kafka
   ├──────────────► access-service :8084 (logs + WebSocket en vivo)
   │                       │ Kafka
   └──────────────► notification-service :8085 (emails de alerta)

eureka-server :8761  ·  PostgreSQL :5432  ·  Redis :6379  ·  Kafka :9092
```

### Topics Kafka activos
`acceso.exitoso` · `acceso.fallido` · `spoofing.detectado` ·
`cuenta.bloqueada` · `ip.sospechosa`

---

## Stack tecnológico

| Capa | Tecnologías |
|------|-------------|
| Backend Java | Java 21, Spring Boot 3.2.5, Spring Cloud 2023.0.0, Spring Security 6 (JWT HS512), JPA, Flyway, Kafka, Redis |
| Biometría facial | pgvector, face-api.js (browser) |
| Backend voz | Python 3.10.11, FastAPI, openai-whisper, librosa, numpy |
| Frontend | React 18, Vite 5, TypeScript, Tailwind, TanStack Query, Zustand, Recharts, Framer Motion |
| Infra | PostgreSQL 16 + pgvector, Redis 7, Apache Kafka 3.6, Eureka |

---

## Requisitos

- **Java 21** y **Maven 3.9**
- **Node 20**
- **Python 3.10.11** + **ffmpeg**
- **PostgreSQL 16** con la extensión **pgvector**
- **Redis 7**
- **Apache Kafka 3.6** + Zookeeper

---

## FASE 1 · Arranque local en Windows (sin Docker)

> Todos los `application.properties` usan `${VARIABLE:valor_local}`, así que
> funcionan sin `.env`. Se necesitan **9 terminales** (o usa el panel de
> Eureka para confirmar que todo quedó registrado).

### 1) Crear las bases de datos

```bash
psql -U postgres -f scripts/init-databases.sql
```

Esto crea `lynx_auth`, `lynx_biometria`, `lynx_accesos` y habilita
`pgvector` en `lynx_biometria`. Flyway crea las tablas al arrancar cada
servicio.

### 2) Modelos de face-api.js

Descarga los pesos desde
<https://github.com/justadudewhohacks/face-api.js/tree/master/weights>
y cópialos en `lynx-ui/public/models/face_api_models/`
(ver el README de esa carpeta). Necesitas:
`ssd_mobilenetv1`, `face_landmark_68` y `face_recognition`.

### 3) Dependencias de Python (voice-service)

```bash
cd voice-service
pip install -r requirements.txt
# o: pip install openai-whisper fastapi uvicorn librosa numpy python-multipart scipy soundfile
# Instala ffmpeg:  choco install ffmpeg  (Windows)  /  sudo apt install ffmpeg  (WSL)
```

### 4) Levantar todo (orden recomendado)

| # | Terminal | Comando |
|---|----------|---------|
| 1 | eureka-server | `cd eureka-server && mvn spring-boot:run` |
| 2 | api-gateway | `cd api-gateway && mvn spring-boot:run` |
| 3 | auth-service | `cd auth-service && mvn spring-boot:run` |
| 4 | face-service | `cd face-service && mvn spring-boot:run` |
| 5 | voice-service | `cd voice-service && python main.py` |
| 6 | access-service | `cd access-service && mvn spring-boot:run` |
| 7 | notification-service | `cd notification-service && mvn spring-boot:run` |
| 8 | lynx-ui | `cd lynx-ui && npm install && npm run dev` |

> Asegúrate de tener PostgreSQL, Redis y Kafka/Zookeeper corriendo antes.

Abre **http://localhost:5173**.

---

## FASE 2 · Docker completo

```bash
docker compose -f docker-compose.full.yml up --build
```

Incluye PostgreSQL (pgvector), Redis, Zookeeper, Kafka, Eureka, gateway,
los 6 microservicios y el frontend (nginx). El frontend queda en
**http://localhost:5173** y el panel Eureka en **http://localhost:8761**.

---

## Cómo usar Lynx

### Registro (una sola vez, 3 pasos)
1. **Datos**: nombre, email, departamento y rol (sin contraseña).
2. **Rostro**: la cámara captura tu rostro y genera el embedding.
3. **Voz**: lees en voz alta la frase mostrada → se guarda tu voiceprint.

### Inicio de sesión (cada vez)
1. **Prueba de vida + rostro**: realiza la acción aleatoria (anti-spoofing)
   y verifica tu rostro.
2. **Voz**: lee la frase aleatoria del momento (anti-replay, expira en 60s).
3. **Acceso concedido** → dashboard de usuario o panel admin según tu rol.

> Usuario administrador inicial sembrado: **admin@lynx.com** (rol ADMIN).
> Debe registrar su biometría para poder entrar.

---

## Seguridad

- **Liveness detection** (anti-foto): instrucción aleatoria verificada en vivo.
- **Anti-replay de voz**: frase rotativa con TTL de 60s en Redis.
- **Bloqueo**: 3 intentos fallidos → cuenta bloqueada 15 min + alertas.
- **Alertas por correo**: spoofing, cuenta bloqueada, IP desconocida, acceso fallido.
- **JWT HS512**: access token 1h + refresh token rotativo 8h (en memoria, sin localStorage).

---

## Estructura del repositorio

```
lynx/
├── eureka-server/          Service Discovery
├── api-gateway/            Gateway + JWT + LoadBalancer
├── auth-service/           Registro, login biométrico, JWT
├── face-service/           Embeddings faciales y de voz (pgvector)
├── voice-service/          Whisper + MFCC (Python FastAPI)
├── access-service/         Logs, alertas, métricas, WebSocket
├── notification-service/   Consumidores Kafka + emails
├── lynx-ui/                Frontend React + cámara + micrófono
├── scripts/init-databases.sql
├── docker-compose.full.yml
├── .env.example
└── pom.xml                 POM padre (BOM)
```

---

## Autor

**Abel Gomez** — © 2026. Todos los derechos reservados.
