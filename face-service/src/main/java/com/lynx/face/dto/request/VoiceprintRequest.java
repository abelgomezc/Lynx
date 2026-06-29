/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.face.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Almacenamiento de un voiceprint (MFCC de 13 componentes). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoiceprintRequest {

    @NotNull(message = "El id de usuario es obligatorio")
    private Long idUsuario;

    @NotEmpty(message = "El voiceprint es obligatorio")
    private List<Double> voiceprint;
}
