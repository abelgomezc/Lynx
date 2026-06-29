/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Respuesta de verificación facial de face-service. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaceVerificacionDto {
    private Long idUsuario;
    private Boolean reconocido;
    private Double confianza;
    private Double distancia;
    private Boolean esSpoofing;
}
