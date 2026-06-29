/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.face.exception;

import org.springframework.http.HttpStatus;

/** Se detectó un intento de suplantación (foto/video en lugar de rostro real). */
public class SpoofingDetectadoException extends LynxException {

    public SpoofingDetectadoException(String mensaje) {
        super("SPOOFING_DETECTADO", mensaje, HttpStatus.FORBIDDEN);
    }
}
