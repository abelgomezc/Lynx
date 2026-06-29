/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.face.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Resultado de un registro o verificación facial. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaceVerificacionResponse {
    private Long idUsuario;
    private Boolean reconocido;
    private Double confianza;
    private Double distancia;
    private Boolean esSpoofing;
}
