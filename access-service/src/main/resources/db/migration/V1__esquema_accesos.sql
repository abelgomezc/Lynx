-- Lynx - access-service - Esquema de accesos y alertas
-- © 2026 Abel Gomez. Todos los derechos reservados.

CREATE TABLE logs_acceso (
    id                  BIGSERIAL PRIMARY KEY,
    id_usuario          BIGINT,
    nombre_usuario      VARCHAR(255),
    ip_address          VARCHAR(45) NOT NULL,
    pais                VARCHAR(100),
    ciudad              VARCHAR(100),
    dispositivo         VARCHAR(255),
    resultado           VARCHAR(20) NOT NULL,
    factor1_exitoso     BOOLEAN,
    factor2_exitoso     BOOLEAN,
    factor_fallido      VARCHAR(20),
    confianza_facial    DECIMAL(5,4),
    confianza_voz       DECIMAL(5,4),
    foto_captura        TEXT,
    es_spoofing         BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE alertas (
    id                  BIGSERIAL PRIMARY KEY,
    tipo                VARCHAR(50) NOT NULL,
    descripcion         TEXT NOT NULL,
    id_usuario          BIGINT,
    ip_address          VARCHAR(45),
    foto_evidencia      TEXT,
    resuelta            BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion      TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_logs_acceso_usuario ON logs_acceso(id_usuario);
CREATE INDEX idx_logs_acceso_fecha ON logs_acceso(fecha_creacion DESC);
CREATE INDEX idx_alertas_tipo ON alertas(tipo);
CREATE INDEX idx_alertas_resuelta ON alertas(resuelta);
