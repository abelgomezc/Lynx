/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.notification.kafka;

import com.lynx.notification.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/** Consume spoofing.detectado y notifica al administrador con la evidencia. */
@Component
@Slf4j
public class SpoofingConsumer {

    @Autowired
    private NotificationService notificationService;

    @KafkaListener(topics = "spoofing.detectado", groupId = "notification-service-group")
    public void consumir(Map<String, Object> evento) {
        log.warn("notification ← spoofing.detectado desde IP {}", evento.get("ipAddress"));
        notificationService.notificarSpoofing(evento);
    }
}
