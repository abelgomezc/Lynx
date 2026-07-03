# 🐾 Lynx · Autenticación Biométrica Dual: Cara + Voz

> **Sin contraseñas. Sin SMS. Sin apps externas.**
> Te registras una vez con tu cara y tu voz. Después, la cámara y el
> micrófono son tu identidad.
>
> **© 2026 Abel Gomez. Todos los derechos reservados.**

---

## ¿Qué es Lynx?

Lynx reemplaza el usuario y la contraseña tradicionales con **dos factores
biométricos** encadenados en un solo flujo de acceso:

1. **Reconocimiento facial** con **face-api.js** en el navegador (embeddings
   de 128 dimensiones) + **prueba de vida activa** (anti-foto).
2. **Reconocimiento de voz** con **Whisper local** (transcripción) + huella
   de voz **MFCC** (librosa), en un microservicio Python.

El procesamiento biométrico es **100% local**: no se envían datos biométricos
a la nube. Los vectores se guardan como embeddings en **pgvector**.

No es una librería: es una **plataforma completa** — registro, login
biométrico, dashboard de usuario, panel de administración, historial de
accesos en tiempo real (WebSocket), alertas de seguridad y correos.

---

## Arquitectura

```
React (lynx-ui :5173)
   │ REST /api  +  WebSocket
   ▼
api-gateway :8080 ── valida JWT · CORS · circuit breaker
   │ lb:// (Eureka)
   ├──────────────► auth-service :8081 ──► face-service :8082  (pgvector)
   │                       │           └─► voice-service :8083 (Python/Whisper)
   │                       │ Kafka
   ├──────────────► access-service :8084 (logs + WebSocket en vivo)
   │                       │ Kafka
   └──────────────► notification-service :8085 (emails de alerta)

eureka-server :8761   ·   PostgreSQL (pgvector)   ·   Redis   ·   Kafka
```

**Topics Kafka:** `acceso.exitoso` · `acceso.fallido` · `spoofing.detectado`
· `cuenta.bloqueada` · `ip.sospechosa`

---

## Stack tecnológico

| Capa | Tecnologías |
|------|-------------|
| Backend Java | Java 21 · Spring Boot 3.2.5 · Spring Cloud 2023.0.0 · Spring Security 6 (JWT HS512) · JPA/Flyway · Kafka · Redis · OpenFeign |
| Biometría facial | **face-api.js** (browser) · **pgvector** (búsqueda por similitud coseno) |
| Backend voz | Python 3.10 · FastAPI · openai-whisper · librosa · numpy |
| Frontend | React 18 · Vite 5 · TypeScript · Tailwind · TanStack Query · Zustand · Recharts · Framer Motion · Canvas 3D |
| Infra | PostgreSQL 16 + pgvector · Redis 7 · Apache Kafka 3.6 · Eureka |

---

## 🔐 Seguridad y anti-spoofing

Lynx no se limita a "¿la cara coincide?" — porque una **foto** produce el
mismo embedding que la persona real. Las defensas implementadas:

- **Prueba de vida ACTIVA verificada en el servidor.** El sistema pide una
  acción aleatoria (**parpadea / abre la boca / gira la cabeza**) y captura
  una serie de métricas por fotograma (EAR de los ojos, MAR de la boca, yaw
  de la cabeza). **El backend (`face-service`) comprueba que la acción
  realmente ocurrió** — una foto o pantalla no puede parpadear ni abrir la
  boca. *Nunca se confía en un "flag" del cliente.*
- **Anti-replay de voz.** La frase a leer se **compone dinámicamente** (miles
  de combinaciones), con TTL de 60 s en Redis en el login → es inviable tener
  una grabación previa exacta.
- **Verificación de voz tolerante pero estricta:** Whisper transcribe y se
  compara (sin tildes/puntuación, ≥82 % de similitud) + huella MFCC (coseno).
- **Bloqueo:** 3 intentos fallidos → cuenta bloqueada 15 min + alertas.
- **Alertas y evidencia:** cada intento de spoofing publica un evento Kafka,
  guarda la foto del intento y envía correo al administrador.
- **JWT HS512:** access token 1 h + refresh token **rotativo** 8 h, guardados
  **solo en memoria** (Zustand), nunca en localStorage.
- **Registro atómico (todo o nada):** el usuario y su biometría se guardan en
  una sola operación; si algo falla, no queda nada a medias.

> ⚠️ **Nota honesta:** el anti-spoofing es una carrera armamentista. Esta
> implementación defiende bien contra **fotos** y ataques casuales, pero para
> producción se recomienda un SDK certificado **ISO 30107-3 (PAD)** y un
> modelo de anti-spoofing pasivo (p. ej. Silent-Face) + anti-deepfake de voz
> (AASIST/RawNet2). La arquitectura ya está lista para enchufarlo.

---

## Requisitos

- **Java 21** y **Maven 3.9**
- **Node 20**
- **Docker Desktop** (para la infraestructura)
- *(Solo modo full-local)* PostgreSQL 16 **con pgvector**, Redis, Kafka, Python 3.10 + ffmpeg

---

## 🚀 Arranque rápido (modo híbrido, recomendado)

Infraestructura + `voice-service` en **Docker**; los 6 servicios Java y el
frontend en **local**. Es lo más cómodo en Windows (evita instalar pgvector y
ffmpeg a mano).

```bash
# 1) Compilar los servicios Java (una vez). En redes con SSL corporativo:
mvn clean install -Dmaven.test.skip=true -Dmaven.resolver.transport=wagon \
  -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true

# 2) Con Docker Desktop abierto, levantar TODO con un comando:
bash scripts/levantar.sh
```

Esto levanta Postgres+pgvector (host **5433**), Redis, Kafka, Zookeeper y
voice-service en Docker, y arranca Eureka, gateway, los microservicios y el
frontend en local. Abre 👉 **http://localhost:5173**

- Guía paso a paso: [LEVANTAR.md](LEVANTAR.md)
- Apagar todo: [DETENER.md](DETENER.md)
- Detalle del modo full-local sin Docker: [ARRANQUE-LOCAL.md](ARRANQUE-LOCAL.md)

### Modelos de face-api.js (una vez)
Descarga los pesos de `ssd_mobilenetv1`, `face_landmark_68` y
`face_recognition` en `lynx-ui/public/models/face_api_models/`
(ver el README de esa carpeta). Sin ellos la cámara enciende pero no genera
el embedding.

---

## 🐳 Arranque todo en Docker (alternativa)

```bash
docker compose -f docker-compose.full.yml up --build
```

Levanta infraestructura + los 6 microservicios Java + voice-service +
frontend (nginx). Frontend en http://localhost:5173, Eureka en :8761.

---

## Cómo se usa

### Registro (una vez, 3 pasos — atómico)
1. **Datos**: nombre, email y departamento (con validación). Sin contraseña.
   Todo usuario nuevo se crea como **EMPLEADO**; el admin puede cambiar el rol.
2. **Rostro**: guía en vivo (semáforo 🔴🟡🟢) para centrarte; captura y
   previsualiza tu foto (puedes repetirla).
3. **Voz**: lee la frase mostrada; puedes **escucharte** y regrabar.
   Al pulsar **Completar registro** se guarda todo de una vez.

### Inicio de sesión (cada vez)
1. **Factor 1 · Rostro + prueba de vida:** realiza la acción indicada
   (parpadea / abre la boca / gira) — el servidor verifica que sea real.
2. **Factor 2 · Voz:** lee la frase del momento (cambia cada vez).
3. **Acceso concedido** → dashboard de usuario o panel admin según tu rol.

> Admin sembrado: **admin@lynx.com** (rol ADMIN). Debe registrar su biometría.

---

## Estructura del repositorio

```
lynx/
├── eureka-server/          Service Discovery
├── api-gateway/            Gateway + JWT + LoadBalancer + circuit breaker
├── auth-service/           Registro atómico, login biométrico, JWT, frases
├── face-service/           Embeddings (pgvector) + liveness server-side
├── voice-service/          Whisper + MFCC (Python FastAPI)
├── access-service/         Logs, alertas, métricas, WebSocket en vivo
├── notification-service/   Consumidores Kafka + emails de alerta
├── lynx-ui/                Frontend React + cámara + micrófono + fondo 3D
├── scripts/
│   ├── levantar.sh         Arranca todo (modo híbrido)
│   ├── arrancar-infra.bat  Solo infraestructura Docker
│   └── init-databases.sql  Crea las 3 BD + pgvector
├── docker-compose.infra.yml   Infra + voice (modo híbrido)
├── docker-compose.full.yml    Todo en Docker
├── LEVANTAR.md · DETENER.md · ARRANQUE-LOCAL.md
└── pom.xml                 POM padre (BOM)
```

---

## Notas de entorno

- **SSL corporativo:** si Maven falla con `PKIX path building failed`, usa los
  flags `-Dmaven.resolver.transport=wagon -Dmaven.wagon.http.ssl.insecure=true`
  (npm: `npm install --strict-ssl=false`).
- **Puerto de Postgres:** en modo híbrido el Postgres de Docker se publica en
  el host en **5433** (para no chocar con un PostgreSQL local en 5432); los
  servicios Java locales apuntan ahí (los scripts ya lo hacen).

---

## Autor

**Abel Gomez** — © 2026. Todos los derechos reservados.