/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.access.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Vista de una alerta de seguridad. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertaResponse {
    private Long id;
    private String tipo;
    private String descripcion;
    private Long idUsuario;
    private String ipAddress;
    private String fotoEvidencia;
    private Boolean resuelta;
    private LocalDateTime fechaCreacion;
}
