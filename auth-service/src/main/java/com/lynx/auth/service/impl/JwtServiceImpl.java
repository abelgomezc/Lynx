/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.service.impl;

import com.lynx.auth.entity.Usuario;
import com.lynx.auth.exception.TokenException;
import com.lynx.auth.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Implementación de generación y validación de JWT HS512. */
@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private SecretKey clave() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generarAccessToken(Usuario usuario, Double confianzaFacial, Double confianzaVoz) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + jwtExpiration);

        Map<String, Object> claims = new HashMap<>();
        claims.put("nombre", usuario.getNombre());
        claims.put("email", usuario.getEmail());
        claims.put("rol", usuario.getRol().name());
        claims.put("metodo_auth", "BIOMETRICO_DUAL");
        claims.put("confianza_facial", confianzaFacial);
        claims.put("confianza_voz", confianzaVoz);

        return Jwts.builder()
                .claims(claims)
                .subject(String.valueOf(usuario.getId()))
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(clave(), Jwts.SIG.HS512)
                .compact();
    }

    @Override
    public String generarRefreshToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public Long extraerIdUsuario(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(clave())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Long.valueOf(claims.getSubject());
        } catch (Exception ex) {
            throw new TokenException("TOKEN_INVALIDO", "No se pudo leer el token: " + ex.getMessage());
        }
    }

    @Override
    public boolean esTokenValido(String token) {
        try {
            Jwts.parser().verifyWith(clave()).build().parseSignedClaims(token);
            return true;
        } catch (Exception ex) {
            log.debug("Token inválido: {}", ex.getMessage());
            return false;
        }
    }
}
