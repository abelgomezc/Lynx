/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Respuesta de verificación de voz del voice-service (Python). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoiceVerificacionDto {

    @JsonProperty("texto_correcto")
    private Boolean textoCorrecto;

    @JsonProperty("voz_verificada")
    private Boolean vozVerificada;

    private String transcripcion;

    @JsonProperty("similitud_voz")
    private Double similitudVoz;

    private Double confianza;

    private Boolean exitoso;
}
