-- Lynx - auth-service - Esquema base
-- © 2026 Abel Gomez. Todos los derechos reservados.

CREATE TABLE usuarios (
    id                  BIGSERIAL PRIMARY KEY,
    nombre              VARCHAR(255) NOT NULL,
    email               VARCHAR(255) UNIQUE NOT NULL,
    rol                 VARCHAR(20) NOT NULL DEFAULT 'EMPLEADO',
    departamento        VARCHAR(100),
    estado_registro     VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE_BIOMETRIA',
    es_activo           BOOLEAN NOT NULL DEFAULT TRUE,
    intentos_fallidos   INTEGER NOT NULL DEFAULT 0,
    bloqueado_hasta     TIMESTAMP,
    fecha_creacion      TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE refresh_tokens (
    id                  BIGSERIAL PRIMARY KEY,
    token               VARCHAR(500) UNIQUE NOT NULL,
    id_usuario          BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    es_valido           BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_expiracion    TIMESTAMP NOT NULL,
    fecha_creacion      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_usuario ON refresh_tokens(id_usuario);
