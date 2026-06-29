/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.access.kafka;

import com.lynx.access.service.AccessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/** Consume los eventos de acceso y seguridad y los persiste/difunde. */
@Component
@Slf4j
public class AccessEventConsumer {

    @Autowired
    private AccessService accessService;

    @KafkaListener(topics = "acceso.exitoso", groupId = "access-service-group")
    public void onAccesoExitoso(Map<String, Object> evento) {
        log.info("Evento acceso.exitoso recibido: {}", evento.get("email"));
        accessService.procesarAccesoExitoso(evento);
    }

    @KafkaListener(topics = "acceso.fallido", groupId = "access-service-group")
    public void onAccesoFallido(Map<String, Object> evento) {
        log.info("Evento acceso.fallido recibido: {}", evento.get("email"));
        accessService.procesarAccesoFallido(evento);
    }

    @KafkaListener(topics = "spoofing.detectado", groupId = "access-service-group")
    public void onSpoofing(Map<String, Object> evento) {
        log.warn("Evento spoofing.detectado recibido desde IP {}", evento.get("ipAddress"));
        accessService.procesarSpoofing(evento);
    }
}
