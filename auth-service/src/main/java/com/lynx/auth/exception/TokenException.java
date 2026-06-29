/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.exception;

import org.springframework.http.HttpStatus;

/** Error relacionado con tokens (refresh inválido o expirado). */
public class TokenException extends LynxException {

    public TokenException(String codigo, String mensaje) {
        super(codigo, mensaje, HttpStatus.UNAUTHORIZED);
    }
}
