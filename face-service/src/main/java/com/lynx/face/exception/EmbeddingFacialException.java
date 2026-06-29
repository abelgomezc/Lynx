/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.face.exception;

import org.springframework.http.HttpStatus;

/** Error al procesar o almacenar un embedding facial. */
public class EmbeddingFacialException extends LynxException {

    public EmbeddingFacialException(String mensaje) {
        super("EMBEDDING_FACIAL_INVALIDO", mensaje, HttpStatus.BAD_REQUEST);
    }
}
