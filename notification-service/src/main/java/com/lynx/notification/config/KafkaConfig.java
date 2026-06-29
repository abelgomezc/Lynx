/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.notification.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

/** Habilita los listeners de Kafka del notification-service. */
@EnableKafka
@Configuration
public class KafkaConfig {
}
