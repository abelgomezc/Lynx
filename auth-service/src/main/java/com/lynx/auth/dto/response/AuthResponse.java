/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta de autenticación. Se reutiliza en los dos factores:
 *  - Tras el factor facial: requiereSegundoFactor=true + frase a leer.
 *  - Tras el factor de voz exitoso: tokens + usuario.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private UsuarioResponse usuario;

    private Double confianzaFacial;
    private Double confianzaVoz;

    /** True cuando el factor facial fue exitoso y falta el factor de voz. */
    private Boolean requiereSegundoFactor;

    /** Frase aleatoria que el usuario debe leer en el factor de voz. */
    private String frase;

    private String mensaje;
}
