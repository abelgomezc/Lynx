/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.face.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Voiceprint recuperado para verificación de voz. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoiceprintResponse {
    private Long idUsuario;
    private List<Double> voiceprint;
}
