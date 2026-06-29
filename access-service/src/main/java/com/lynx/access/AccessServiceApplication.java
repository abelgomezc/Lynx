/*
 * Lynx - Sistema de Autenticación Biométrica Dual: Cara + Voz
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.access;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * access-service: consume eventos de Kafka, registra el historial de
 * accesos, genera alertas, expone métricas y transmite los accesos en
 * vivo por WebSocket.
 */
@EnableJpaAuditing
@EnableDiscoveryClient
@SpringBootApplication
public class AccessServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccessServiceApplication.class, args);
    }
}
