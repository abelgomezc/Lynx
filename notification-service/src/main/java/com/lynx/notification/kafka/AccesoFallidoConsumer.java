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

/** Consume acceso.fallido y notifica al usuario. */
@Component
@Slf4j
public class AccesoFallidoConsumer {

    @Autowired
    private NotificationService notificationService;

    @KafkaListener(topics = "acceso.fallido", groupId = "notification-service-group")
    public void consumir(Map<String, Object> evento) {
        log.info("notification ← acceso.fallido para {}", evento.get("email"));
        notificationService.notificarAccesoFallido(evento);
    }
}
