/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.service;

import com.lynx.auth.entity.Usuario;

/** Generación y validación de tokens JWT (HS512). */
public interface JwtService {

    String generarAccessToken(Usuario usuario, Double confianzaFacial, Double confianzaVoz);

    String generarRefreshToken();

    Long extraerIdUsuario(String token);

    boolean esTokenValido(String token);
}
