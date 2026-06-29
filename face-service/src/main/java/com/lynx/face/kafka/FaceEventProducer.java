/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.face.kafka;

import com.lynx.face.config.KafkaConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/** Publica eventos de seguridad de face-service en Kafka. */
@Component
@Slf4j
public class FaceEventProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void publicarSpoofing(String ipAddress, String pais, String fotoCaptura, String tipoSpoofing) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("ipAddress", ipAddress);
        payload.put("pais", pais);
        payload.put("fotoCaptura", fotoCaptura);
        payload.put("tipoSpoofing", tipoSpoofing);
        payload.put("timestamp", LocalDateTime.now().toString());
        try {
            kafkaTemplate.send(KafkaConfig.TOPIC_SPOOFING_DETECTADO, payload);
            log.warn("Evento spoofing.detectado publicado para IP {}", ipAddress);
        } catch (Exception ex) {
            log.error("No se pudo publicar spoofing.detectado: {}", ex.getMessage());
        }
    }
}
