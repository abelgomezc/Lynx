/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.face.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Verificación facial con datos de liveness anti-spoofing. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificarFaceRequest {

    @NotEmpty(message = "El embedding es obligatorio")
    private List<Double> embedding;

    /** Acción de liveness solicitada (p. ej. "Parpadea dos veces"). */
    private String accionLiveness;

    /** Si el frontend confirmó que la acción de liveness se ejecutó. */
    private Boolean livenessSuperado;

    /** Foto del intento en base64 (para evidencia de spoofing). */
    private String fotoCaptura;

    private String ipAddress;
}
