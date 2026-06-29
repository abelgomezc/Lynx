/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.exception;

import org.springframework.http.HttpStatus;

/** Error durante el proceso de registro de usuario o de biometría. */
public class RegistroException extends LynxException {

    public RegistroException(String codigo, String mensaje) {
        super(codigo, mensaje, HttpStatus.BAD_REQUEST);
    }
}
