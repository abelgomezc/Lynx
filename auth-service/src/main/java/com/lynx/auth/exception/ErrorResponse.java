/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.exception;

import java.time.LocalDateTime;

/** Estructura uniforme de error devuelta por los microservicios Lynx. */
public record ErrorResponse(
        String codigo,
        String mensaje,
        LocalDateTime timestamp,
        String servicio,
        String ruta
) {
}
