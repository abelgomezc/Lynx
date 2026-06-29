-- Lynx - face-service - Esquema biométrico (pgvector)
-- © 2026 Abel Gomez. Todos los derechos reservados.

CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE embeddings_faciales (
    id                  BIGSERIAL PRIMARY KEY,
    id_usuario          BIGINT NOT NULL,
    embedding           vector(128),
    foto_referencia     TEXT,
    es_activo           BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion      TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE embeddings_voz (
    id                  BIGSERIAL PRIMARY KEY,
    id_usuario          BIGINT NOT NULL,
    mfcc_features       vector(13),
    es_activo           BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion      TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX ON embeddings_faciales
    USING ivfflat (embedding vector_cosine_ops)
    WITH (lists = 50);

CREATE INDEX ON embeddings_voz
    USING ivfflat (mfcc_features vector_cosine_ops)
    WITH (lists = 50);

CREATE INDEX idx_embeddings_faciales_usuario ON embeddings_faciales(id_usuario);
CREATE INDEX idx_embeddings_voz_usuario ON embeddings_voz(id_usuario);
