/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Factor 2: datos de la verificación de voz. El audio viaja como
 * multipart aparte; aquí se envían los metadatos del intento.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginVoiceRequest {

    private Long idUsuario;

    private String fraseEsperada;

    private Double confianzaFacial;

    private String ipAddress;

    private String dispositivo;
}
