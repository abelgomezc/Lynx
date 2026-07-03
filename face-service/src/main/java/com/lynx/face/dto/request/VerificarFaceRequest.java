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

/** Verificación facial con evidencia de prueba de vida (anti-spoofing). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificarFaceRequest {

    @NotEmpty(message = "El embedding es obligatorio")
    private List<Double> embedding;

    /** Acción de liveness solicitada (PARPADEA, ABRE_BOCA, GIRA_CABEZA). */
    private String accionLiveness;

    /** Serie de métricas por fotograma; el servidor verifica que la acción ocurrió. */
    private List<MuestraLivenessDto> muestrasLiveness;

    /** Foto del intento en base64 (para evidencia de spoofing). */
    private String fotoCaptura;

    private String ipAddress;
}
