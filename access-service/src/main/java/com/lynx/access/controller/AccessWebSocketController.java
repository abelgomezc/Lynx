/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.access.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Handler WebSocket que transmite los accesos en tiempo real al panel
 * de administración (ruta /access/live).
 */
@Component
@Slf4j
public class AccessWebSocketController extends TextWebSocketHandler {

    private final Set<WebSocketSession> sesiones = new CopyOnWriteArraySet<>();

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sesiones.add(session);
        log.info("Cliente WebSocket conectado: {} (total {})", session.getId(), sesiones.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sesiones.remove(session);
        log.info("Cliente WebSocket desconectado: {} (total {})", session.getId(), sesiones.size());
    }

    /** Difunde un objeto serializado a JSON a todas las sesiones activas. */
    public void difundir(Object payload) {
        if (sesiones.isEmpty()) {
            return;
        }
        try {
            TextMessage mensaje = new TextMessage(objectMapper.writeValueAsString(payload));
            for (WebSocketSession sesion : sesiones) {
                if (sesion.isOpen()) {
                    try {
                        sesion.sendMessage(mensaje);
                    } catch (IOException ex) {
                        log.warn("No se pudo enviar a sesión {}: {}", sesion.getId(), ex.getMessage());
                    }
                }
            }
        } catch (Exception ex) {
            log.error("Error serializando mensaje WebSocket: {}", ex.getMessage());
        }
    }
}
