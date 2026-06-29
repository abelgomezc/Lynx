/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.face.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/** Topics Kafka producidos por face-service. */
@Configuration
public class KafkaConfig {

    public static final String TOPIC_SPOOFING_DETECTADO = "spoofing.detectado";

    @Bean
    public NewTopic spoofingDetectadoTopic() {
        return TopicBuilder.name(TOPIC_SPOOFING_DETECTADO).partitions(1).replicas(1).build();
    }
}
