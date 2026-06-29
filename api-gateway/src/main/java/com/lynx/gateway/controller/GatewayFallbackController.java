/*
 * Lynx - Sistema de Autenticación Biométrica Dual: Cara + Voz
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Respuestas de respaldo (fallback) cuando un microservicio no responde
 * y el circuit breaker abre el circuito.
 */
@RestController
@RequestMapping("/fallback")
public class GatewayFallbackController {

    @GetMapping("/auth")
    public Mono<ResponseEntity<Map<String, Object>>> authFallback() {
        return construir("auth-service");
    }

    @GetMapping("/access")
    public Mono<ResponseEntity<Map<String, Object>>> accessFallback() {
        return construir("access-service");
    }

    @GetMapping("/admin")
    public Mono<ResponseEntity<Map<String, Object>>> adminFallback() {
        return construir("auth-service (admin)");
    }

    private Mono<ResponseEntity<Map<String, Object>>> construir(String servicio) {
        Map<String, Object> body = Map.of(
                "codigo", "SERVICIO_NO_DISPONIBLE",
                "mensaje", "El servicio " + servicio + " no está disponible en este momento",
                "timestamp", LocalDateTime.now().toString(),
                "servicio", "api-gateway"
        );
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body));
    }
}
