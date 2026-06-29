/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.notification.service;

import java.util.Map;

/** Envío de correos de alerta a partir de los eventos de Kafka. */
public interface NotificationService {

    void notificarAccesoFallido(Map<String, Object> evento);

    void notificarSpoofing(Map<String, Object> evento);

    void notificarCuentaBloqueada(Map<String, Object> evento);

    void notificarIpDesconocida(Map<String, Object> evento);
}
