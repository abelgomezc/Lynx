/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.dto.request;

import com.lynx.auth.dto.MuestraLivenessDto;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Factor 1: embedding facial + evidencia de prueba de vida (anti-spoofing). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginFaceRequest {

    @NotEmpty(message = "El embedding facial es obligatorio")
    private List<Double> embedding;

    /** Acción de liveness solicitada (PARPADEA, ABRE_BOCA, GIRA_CABEZA). */
    private String accionLiveness;

    /** Serie de métricas por fotograma para verificar la prueba de vida. */
    private List<MuestraLivenessDto> muestrasLiveness;

    private String ipAddress;

    private String dispositivo;
}
