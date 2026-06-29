-- =====================================================================
--  Lynx - Inicialización de bases de datos
--  © 2026 Abel Gomez. Todos los derechos reservados.
--
--  Ejecutar conectado a PostgreSQL como superusuario:
--      psql -U postgres -f scripts/init-databases.sql
--
--  Crea las 3 bases de datos del sistema y habilita pgvector
--  en la base de datos biométrica.
-- =====================================================================

-- Las tres bases de datos del sistema
CREATE DATABASE lynx_auth;
CREATE DATABASE lynx_biometria;
CREATE DATABASE lynx_accesos;

-- Conectarse a la base biométrica y habilitar la extensión vector.
-- NOTA: en psql el comando \c funciona; si se ejecuta desde otra
-- herramienta, conectarse manualmente a lynx_biometria y correr:
--     CREATE EXTENSION IF NOT EXISTS vector;
\c lynx_biometria
CREATE EXTENSION IF NOT EXISTS vector;
