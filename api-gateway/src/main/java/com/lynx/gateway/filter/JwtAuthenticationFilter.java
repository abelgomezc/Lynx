/*
 * Lynx - Sistema de Autenticación Biométrica Dual: Cara + Voz
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Filtro global que valida el JWT (HS512) en cada petición.
 * Las rutas públicas (registro y login biométrico, refresh, health y
 * fallback) se dejan pasar sin token. Las rutas /admin/** exigen rol ADMIN.
 * Si el token es válido, propaga datos del usuario en cabeceras internas.
 */
@Slf4j
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String jwtSecret;

    // Rutas que NO requieren JWT
    private static final List<String> RUTAS_PUBLICAS = List.of(
            "/auth/register",
            "/auth/register/face",
            "/auth/register/voice",
            "/auth/login/face",
            "/auth/login/voice",
            "/auth/refresh",
            "/auth/frase",
            "/actuator/health",
            "/fallback"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Rutas públicas y preflight CORS pasan sin validación
        if (esRutaPublica(path) || "OPTIONS".equalsIgnoreCase(request.getMethod().name())) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return denegar(exchange, "Falta el token de autenticación", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = parseClaims(token);

            // Control de acceso por rol para rutas de administración
            String rol = claims.get("rol", String.class);
            if (path.startsWith("/admin") && !"ADMIN".equals(rol)) {
                return denegar(exchange, "Acceso restringido a administradores", HttpStatus.FORBIDDEN);
            }

            // Propagar identidad a los microservicios aguas abajo
            ServerHttpRequest mutado = request.mutate()
                    .header("X-Usuario-Id", String.valueOf(claims.getSubject()))
                    .header("X-Usuario-Email", claims.get("email", String.class) != null
                            ? claims.get("email", String.class) : "")
                    .header("X-Usuario-Rol", rol != null ? rol : "")
                    .build();

            return chain.filter(exchange.mutate().request(mutado).build());
        } catch (Exception ex) {
            log.warn("JWT inválido o expirado: {}", ex.getMessage());
            return denegar(exchange, "Token inválido o expirado", HttpStatus.UNAUTHORIZED);
        }
    }

    private Claims parseClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean esRutaPublica(String path) {
        return RUTAS_PUBLICAS.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> denegar(ServerWebExchange exchange, String mensaje, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        String body = String.format(
                "{\"codigo\":\"%s\",\"mensaje\":\"%s\",\"servicio\":\"api-gateway\"}",
                status.name(), mensaje);
        var buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        // Antes del enrutamiento
        return -1;
    }
}
