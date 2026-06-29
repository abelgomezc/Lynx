/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Respuesta de registro de voz del voice-service (Python). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoiceRegistroDto {

    @JsonProperty("id_usuario")
    private Long idUsuario;

    @JsonProperty("texto_correcto")
    private Boolean textoCorrecto;

    private String transcripcion;

    private List<Double> voiceprint;

    private Boolean exitoso;
}
