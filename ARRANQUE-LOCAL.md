# 🚀 Lynx · Guía de arranque LOCAL (Windows, sin Docker)

> **© 2026 Abel Gomez. Todos los derechos reservados.**
>
> Esta guía levanta **todo Lynx en tu máquina**, sin contenedores.
> Si prefieres Docker, mira la sección [👉 ¿Local o Docker?](#-local-o-docker).

---

## 🧭 ¿Local o Docker?

Lynx puede correr de **dos formas** y **no necesitas las dos a la vez**:

| Forma | Cuándo usarla | Qué arranca |
|-------|---------------|-------------|
| **LOCAL (esta guía)** | Desarrollo diario, depurar, ver logs | Tú levantas cada servicio a mano (PostgreSQL, Redis, Kafka + los 8 servicios de Lynx) |
| **DOCKER** | Demostrar el proyecto / portafolio, levantar todo de un golpe | Un solo comando levanta **todo** (infra + servicios) en contenedores |

⚠️ **No mezcles ambas a la vez**: las dos usan los mismos puertos (5432, 6379,
9092, 8080…). Si Docker está corriendo, apágalo antes de arrancar en local, y
viceversa.

- **Docker** (todo automático, nada que instalar salvo Docker):
  ```bash
  docker compose -f docker-compose.full.yml up --build
  ```
  Frontend en http://localhost:5173.

- **Local**: sigue el resto de este documento. 👇

---

## ✅ Requisitos (instalar una sola vez)

| Herramienta | Versión | Verificar |
|-------------|---------|-----------|
| Java (JDK) | 21 | `java -version` |
| Maven | 3.9+ | `mvn -version` |
| Node.js | 20 | `node -v` |
| Python | 3.10.11 | `python --version` |
| ffmpeg | cualquiera | `ffmpeg -version` |
| PostgreSQL | 16 (con pgvector) | `psql --version` |
| Redis | 7 | (en WSL) `redis-cli ping` → `PONG` |
| Kafka + Zookeeper | 3.6 | — |

> **pgvector**: PostgreSQL normal **no trae** pgvector. En Windows instálalo
> con StackBuilder o el instalador de pgvector. Si te complica, lo más fácil
> es levantar **solo PostgreSQL en Docker** (ver el final) y el resto en local.

---

## 1️⃣ Infraestructura (PostgreSQL, Redis, Kafka)

### a) PostgreSQL + bases de datos

1. Arranca PostgreSQL (servicio de Windows o `pg_ctl start`).
2. Crea las 3 bases de datos y habilita pgvector:

   ```bash
   psql -U postgres -f scripts/init-databases.sql
   ```

   Crea `lynx_auth`, `lynx_biometria`, `lynx_accesos` y activa la extensión
   `vector` en `lynx_biometria`. Las **tablas** se crean solas (Flyway) cuando
   arranca cada servicio.

   > Si tu contraseña de `postgres` **no** es `postgres`, expórtala antes de
   > arrancar los servicios (ver [variables opcionales](#-variables-opcionales)).

### b) Redis (en WSL Ubuntu)

```bash
# Dentro de WSL
sudo service redis-server start
redis-cli ping        # debe responder PONG
```

> Los servicios esperan Redis con clave `redis_lynx_2024`. Si tu Redis local
> **no tiene clave**, expórtala vacía (ver variables opcionales) o pon
> `requirepass redis_lynx_2024` en tu `redis.conf`.

### c) Kafka + Zookeeper (en Windows)

Desde la carpeta de Kafka, en **dos terminales**:

```bash
# Terminal Zookeeper
.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties

# Terminal Kafka
.\bin\windows\kafka-server-start.bat .\config\server.properties
```

> No hace falta crear los topics a mano: se crean automáticamente
> (`acceso.exitoso`, `acceso.fallido`, `spoofing.detectado`,
> `cuenta.bloqueada`, `ip.sospechosa`).

---

## 2️⃣ Preparación de una sola vez

### a) Modelos de face-api.js (reconocimiento facial)

Descarga los pesos desde
<https://github.com/justadudewhohacks/face-api.js/tree/master/weights>
y cópialos en:

```
lynx-ui/public/models/face_api_models/
```

Necesitas las familias `ssd_mobilenetv1`, `face_landmark_68` y
`face_recognition` (cada `*-weights_manifest.json` con sus shards).
Sin estos modelos, la cámara enciende pero **no genera el embedding**.

### b) Dependencias de Python (voice-service)

```bash
cd voice-service
python -m venv .venv
.venv\Scripts\activate          # Windows
pip install -r requirements.txt
```

La **primera** vez que arranque, Whisper descargará el modelo `base`
(una sola vez, tarda un poco).

### c) Dependencias del frontend

```bash
cd lynx-ui
npm install
```

---

## 3️⃣ Arrancar Lynx (orden recomendado)

Abre **8 terminales**. Respeta el orden: primero Eureka, luego el resto.

| # | Servicio | Carpeta | Comando | Puerto |
|---|----------|---------|---------|--------|
| 1 | Eureka | `eureka-server` | `mvn spring-boot:run` | 8761 |
| 2 | Gateway | `api-gateway` | `mvn spring-boot:run` | 8080 |
| 3 | Auth | `auth-service` | `mvn spring-boot:run` | 8081 |
| 4 | Face | `face-service` | `mvn spring-boot:run` | 8082 |
| 5 | Voice | `voice-service` | `python main.py` | 8083 |
| 6 | Access | `access-service` | `mvn spring-boot:run` | 8084 |
| 7 | Notification | `notification-service` | `mvn spring-boot:run` | 8085 |
| 8 | Frontend | `lynx-ui` | `npm run dev` | 5173 |

> 💡 Espera ~20-30s entre Eureka (1) y los demás para que el registro se
> complete. Confírmalo en el panel de Eureka: **http://localhost:8761**

Cuando todos estén arriba, abre 👉 **http://localhost:5173**

---

## 4️⃣ Probar el flujo

1. **Registro** (`/register`): datos → captura tu rostro → lee la frase en voz alta.
2. **Login** (`/login`): prueba de vida + rostro → lee la frase del momento → acceso concedido.
3. Según tu rol entras al **dashboard** (usuario) o al **panel admin**.

> Hay un admin sembrado: `admin@lynx.com` (rol ADMIN). Debe registrar su
> biometría para poder entrar.

---

## 🔧 Variables opcionales

Solo si tu entorno difiere de los valores por defecto. En PowerShell se
exportan **antes** de `mvn spring-boot:run` en esa misma terminal:

```powershell
# Ejemplo: contraseña de Postgres distinta
$env:POSTGRES_PASSWORD = "tu_clave"

# Ejemplo: Redis sin clave
$env:REDIS_PASSWORD = ""

# Ejemplo: correo real para las alertas (notification-service)
$env:MAIL_USER = "tu_email@gmail.com"
$env:MAIL_PASSWORD = "tu_app_password"
```

En Git Bash:

```bash
export POSTGRES_PASSWORD=tu_clave
export REDIS_PASSWORD=""
```

Todos tienen valor por defecto (`${VARIABLE:valor_local}`), así que si tu
entorno coincide con lo estándar, **no necesitas exportar nada**.

---

## 🩺 Problemas comunes

| Síntoma | Causa probable | Solución |
|---------|----------------|----------|
| `relation ... does not exist` | Flyway no corrió / BD equivocada | Verifica que existan las 3 BD y reinicia el servicio |
| `extension "vector" is not available` | pgvector no instalado | Instala pgvector o usa Postgres en Docker |
| `Connection refused :9092` | Kafka apagado | Arranca Zookeeper y Kafka |
| `NOAUTH Authentication required` | Clave de Redis distinta | Ajusta `REDIS_PASSWORD` |
| La cámara abre pero no reconoce | Faltan modelos face-api.js | Copia los pesos a `public/models/face_api_models` |
| El email no llega | SMTP de prueba | Es normal en local; el envío se registra en el log y continúa |
| El front no llama al backend | Gateway apagado | El proxy `/api` apunta a `localhost:8080` |

---

## 🐳 Atajo: solo la infraestructura en Docker

Si no quieres instalar Postgres/Redis/Kafka en Windows, levanta **solo la
infraestructura** en Docker y corre los servicios de Lynx en local:

```bash
docker compose -f docker-compose.full.yml up postgres redis zookeeper kafka
```

Luego arranca los 8 servicios de Lynx en local como en el paso 3️⃣.

---

**© 2026 Abel Gomez. Todos los derechos reservados.**
