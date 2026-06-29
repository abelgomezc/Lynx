/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.exception;

import org.springframework.http.HttpStatus;

/** Error genérico de autenticación biométrica. */
public class AuthenticationException extends LynxException {

    public AuthenticationException(String codigo, String mensaje) {
        super(codigo, mensaje, HttpStatus.UNAUTHORIZED);
    }
}
