/*
 * Lynx - Sistema de Autenticación Biométrica Dual: Cara + Voz
 * © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.face;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * face-service: almacena y compara embeddings faciales y de voz en
 * pgvector, y detecta intentos de suplantación (spoofing).
 */
@EnableJpaAuditing
@EnableDiscoveryClient
@SpringBootApplication
public class FaceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FaceServiceApplication.class, args);
    }
}
