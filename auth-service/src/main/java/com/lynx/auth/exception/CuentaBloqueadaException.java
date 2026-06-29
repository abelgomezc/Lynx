/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.exception;

import org.springframework.http.HttpStatus;

/** La cuenta está bloqueada temporalmente por intentos fallidos. */
public class CuentaBloqueadaException extends LynxException {

    public CuentaBloqueadaException(String mensaje) {
        super("CUENTA_BLOQUEADA", mensaje, HttpStatus.LOCKED);
    }
}
