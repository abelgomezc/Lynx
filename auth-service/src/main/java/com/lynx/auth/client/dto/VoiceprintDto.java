/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Voiceprint (vector MFCC de 13 componentes) almacenado en face-service. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoiceprintDto {
    private Long idUsuario;
    private List<Double> voiceprint;
}
