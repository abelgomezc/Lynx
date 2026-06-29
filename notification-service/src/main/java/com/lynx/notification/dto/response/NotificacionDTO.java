/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.notification.dto.response;

import com.lynx.notification.enums.TipoNotificacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Representa una notificación enviada (para trazabilidad/logs). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDTO {
    private TipoNotificacion tipo;
    private String destinatario;
    private String asunto;
    private Boolean enviada;
    private LocalDateTime fecha;
}
