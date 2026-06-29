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

/** Consume cuenta.bloqueada y notifica al usuario y al administrador. */
@Component
@Slf4j
public class CuentaBloqueadaConsumer {

    @Autowired
    private NotificationService notificationService;

    @KafkaListener(topics = "cuenta.bloqueada", groupId = "notification-service-group")
    public void consumir(Map<String, Object> evento) {
        log.warn("notification ← cuenta.bloqueada para {}", evento.get("email"));
        notificationService.notificarCuentaBloqueada(evento);
    }
}
