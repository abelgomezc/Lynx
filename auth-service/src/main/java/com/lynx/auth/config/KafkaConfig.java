/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/** Declaración de los topics Kafka producidos por auth-service. */
@Configuration
public class KafkaConfig {

    public static final String TOPIC_ACCESO_EXITOSO = "acceso.exitoso";
    public static final String TOPIC_ACCESO_FALLIDO = "acceso.fallido";
    public static final String TOPIC_CUENTA_BLOQUEADA = "cuenta.bloqueada";

    @Bean
    public NewTopic accesoExitosoTopic() {
        return TopicBuilder.name(TOPIC_ACCESO_EXITOSO).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic accesoFallidoTopic() {
        return TopicBuilder.name(TOPIC_ACCESO_FALLIDO).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic cuentaBloqueadaTopic() {
        return TopicBuilder.name(TOPIC_CUENTA_BLOQUEADA).partitions(1).replicas(1).build();
    }
}
