/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.access.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Habilita los listeners de Kafka. La configuración del consumidor
 * (deserializadores, grupo, paquetes confiables) se define en
 * application.properties.
 */
@EnableKafka
@Configuration
public class KafkaConfig {

    public static final String TOPIC_ACCESO_EXITOSO = "acceso.exitoso";
    public static final String TOPIC_ACCESO_FALLIDO = "acceso.fallido";
    public static final String TOPIC_SPOOFING_DETECTADO = "spoofing.detectado";
    public static final String TOPIC_IP_SOSPECHOSA = "ip.sospechosa";
}
