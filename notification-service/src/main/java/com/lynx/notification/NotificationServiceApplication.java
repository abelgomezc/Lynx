/*
 * Lynx - Sistema de Autenticación Biométrica Dual: Cara + Voz
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * notification-service: consume eventos de seguridad de Kafka y envía
 * correos de alerta (acceso fallido, spoofing, cuenta bloqueada, IP
 * desconocida) con plantillas HTML de identidad Lynx.
 */
@EnableDiscoveryClient
@SpringBootApplication
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
