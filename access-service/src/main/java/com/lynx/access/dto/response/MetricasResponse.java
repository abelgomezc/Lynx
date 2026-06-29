/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.access.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Métricas agregadas para el dashboard de administración. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricasResponse {
    private long accesosHoy;
    private long exitososHoy;
    private long fallidosHoy;
    private long alertasActivas;
    private long spoofingHoy;
}
