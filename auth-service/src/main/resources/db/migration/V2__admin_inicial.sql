-- Lynx - auth-service - Administrador inicial
-- © 2026 Abel Gomez. Todos los derechos reservados.
-- Admin inicial: admin@lynx.com / sin contraseña (acceso biométrico)
-- Estado COMPLETO para poder entrar desde el principio con datos mock.
INSERT INTO usuarios (nombre, email, rol, departamento, estado_registro, es_activo)
VALUES ('Administrador Lynx', 'admin@lynx.com', 'ADMIN', 'Sistemas', 'COMPLETO', true);
