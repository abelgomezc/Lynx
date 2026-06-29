/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.face.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/** Excepción base del dominio Lynx en face-service. */
@Getter
public class LynxException extends RuntimeException {

    private final String codigo;
    private final HttpStatus status;

    public LynxException(String codigo, String mensaje, HttpStatus status) {
        super(mensaje);
        this.codigo = codigo;
        this.status = status;
    }
}
