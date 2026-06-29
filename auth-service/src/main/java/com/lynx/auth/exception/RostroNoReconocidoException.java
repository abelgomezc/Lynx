/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.exception;

/** El rostro presentado no coincide con ningún usuario registrado. */
public class RostroNoReconocidoException extends AuthenticationException {

    public RostroNoReconocidoException(String mensaje) {
        super("ROSTRO_NO_RECONOCIDO", mensaje);
    }
}
