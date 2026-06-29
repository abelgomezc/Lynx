/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.access.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Vista de un registro de acceso. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogAccesoResponse {
    private Long id;
    private Long idUsuario;
    private String nombreUsuario;
    private String ipAddress;
    private String pais;
    private String ciudad;
    private String dispositivo;
    private String resultado;
    private Boolean factor1Exitoso;
    private Boolean factor2Exitoso;
    private String factorFallido;
    private BigDecimal confianzaFacial;
    private BigDecimal confianzaVoz;
    private Boolean esSpoofing;
    private LocalDateTime fechaCreacion;
}
