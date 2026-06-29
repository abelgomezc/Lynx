/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.access.service;

import com.lynx.access.dto.response.LogAccesoResponse;
import com.lynx.access.dto.response.MetricasResponse;

import java.util.List;
import java.util.Map;

/** Registro y consulta de accesos, métricas y eventos de seguridad. */
public interface AccessService {

    /** Procesa un evento de acceso exitoso recibido por Kafka. */
    void procesarAccesoExitoso(Map<String, Object> evento);

    /** Procesa un evento de acceso fallido recibido por Kafka. */
    void procesarAccesoFallido(Map<String, Object> evento);

    /** Procesa un evento de spoofing recibido por Kafka. */
    void procesarSpoofing(Map<String, Object> evento);

    List<LogAccesoResponse> historialUsuario(Long idUsuario);

    List<LogAccesoResponse> todos();

    MetricasResponse metricas();
}
