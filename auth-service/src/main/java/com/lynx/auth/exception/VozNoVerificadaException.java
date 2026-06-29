/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.exception;

/** La voz presentada no supera la verificación (texto o voiceprint). */
public class VozNoVerificadaException extends AuthenticationException {

    public VozNoVerificadaException(String mensaje) {
        super("VOZ_NO_VERIFICADA", mensaje);
    }
}
