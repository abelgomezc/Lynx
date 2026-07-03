/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.face.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Métrica de prueba de vida de un fotograma: EAR (ojos), MAR (boca), yaw (giro). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MuestraLivenessDto {
    private Double ear;
    private Double mar;
    private Double yaw;
}