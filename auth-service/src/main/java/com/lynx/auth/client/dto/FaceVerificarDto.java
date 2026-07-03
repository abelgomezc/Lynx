/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.client.dto;

import com.lynx.auth.dto.MuestraLivenessDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Petición de verificación facial enviada a face-service. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaceVerificarDto {
    private List<Double> embedding;
    private String accionLiveness;
    private List<MuestraLivenessDto> muestrasLiveness;
    private String fotoCaptura;
    private String ipAddress;
}
