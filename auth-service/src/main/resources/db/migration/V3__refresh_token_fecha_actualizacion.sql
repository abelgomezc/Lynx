-- Lynx - auth-service - Añade auditoría de actualización a refresh_tokens
-- © 2026 Abel Gomez. Todos los derechos reservados.
-- RefreshToken hereda de BaseEntity (fecha_actualizacion); la tabla original
-- no incluía esa columna. Se agrega para que la validación de esquema pase.
ALTER TABLE refresh_tokens
    ADD COLUMN IF NOT EXISTS fecha_actualizacion TIMESTAMP NOT NULL DEFAULT NOW();