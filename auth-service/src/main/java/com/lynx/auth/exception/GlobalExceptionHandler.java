/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.exception;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/** Manejador global de excepciones del auth-service. */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String SERVICIO = "auth-service";

    @ExceptionHandler(LynxException.class)
    public ResponseEntity<ErrorResponse> handleLynx(LynxException ex, HttpServletRequest req) {
        log.error("LynxException [{}]: {}", ex.getCodigo(), ex.getMessage());
        return ResponseEntity.status(ex.getStatus())
                .body(new ErrorResponse(ex.getCodigo(), ex.getMessage(),
                        LocalDateTime.now(), SERVICIO, req.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest req) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("VALIDACION_FALLIDA", msg,
                        LocalDateTime.now(), SERVICIO, req.getRequestURI()));
    }

    /**
     * Reenvía tal cual los errores de servicios dependientes (face/voice)
     * con su estado y mensaje originales. Así, p. ej., un spoofing (403)
     * detectado en face-service llega al cliente como 403 y no como 500.
     */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<String> handleFeign(FeignException ex) {
        int status = ex.status() > 0 ? ex.status() : HttpStatus.BAD_GATEWAY.value();
        String body = ex.contentUTF8();
        log.warn("Error de servicio dependiente [{}]: {}", status, body);
        if (body == null || body.isBlank()) {
            body = "{\"codigo\":\"SERVICIO_DEPENDIENTE\",\"mensaje\":\"Error en un servicio dependiente\"}";
        }
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Error inesperado: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("ERROR_INTERNO", "Error interno del servidor",
                        LocalDateTime.now(), SERVICIO, req.getRequestURI()));
    }
}
