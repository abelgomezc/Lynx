/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.exception;

import org.springframework.http.HttpStatus;

/** No se encontró el usuario solicitado. */
public class UsuarioNotFoundException extends LynxException {

    public UsuarioNotFoundException(String mensaje) {
        super("USUARIO_NO_ENCONTRADO", mensaje, HttpStatus.NOT_FOUND);
    }
}
