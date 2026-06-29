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

/** Consume ip.sospechosa y notifica al usuario y al administrador. */
@Component
@Slf4j
public class IpSospechosaConsumer {

    @Autowired
    private NotificationService notificationService;

    @KafkaListener(topics = "ip.sospechosa", groupId = "notification-service-group")
    public void consumir(Map<String, Object> evento) {
        log.warn("notification ← ip.sospechosa: {}", evento.get("ipAddress"));
        notificationService.notificarIpDesconocida(evento);
    }
}
