/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.access.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Petición para registrar manualmente un acceso (uso interno/pruebas). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarAccesoRequest {
    private Long idUsuario;
    private String nombreUsuario;
    private String ipAddress;
    private String dispositivo;
    private String resultado;
    private Boolean factor1Exitoso;
    private Boolean factor2Exitoso;
    private String factorFallido;
    private BigDecimal confianzaFacial;
    private BigDecimal confianzaVoz;
    private Boolean esSpoofing;
}
