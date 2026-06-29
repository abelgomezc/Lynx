/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.face.exception;

import java.time.LocalDateTime;

/** Estructura uniforme de error. */
public record ErrorResponse(
        String codigo,
        String mensaje,
        LocalDateTime timestamp,
        String servicio,
        String ruta
) {
}
