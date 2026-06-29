/*
 * Lynx - Sistema de Autenticación Biométrica Dual: Cara + Voz
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API Gateway de Lynx.
 * Punto único de entrada: enruta a los microservicios vía Eureka (lb://),
 * valida el JWT en cada petición protegida y aplica CORS y circuit breaker.
 */
@EnableDiscoveryClient
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
