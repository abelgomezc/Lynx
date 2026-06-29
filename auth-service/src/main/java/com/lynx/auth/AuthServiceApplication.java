/*
 * Lynx - Sistema de Autenticación Biométrica Dual: Cara + Voz
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * auth-service: registro de usuarios, autenticación biométrica dual
 * (cara + voz), emisión y rotación de tokens JWT.
 */
@EnableJpaAuditing
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
