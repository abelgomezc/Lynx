/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.access.service;

import com.lynx.access.dto.response.AlertaResponse;
import com.lynx.access.entity.Alerta;

import java.util.List;

/** Gestión de alertas de seguridad. */
public interface AlertaService {

    Alerta crear(String tipo, String descripcion, Long idUsuario, String ipAddress, String fotoEvidencia);

    List<AlertaResponse> listarActivas();

    List<AlertaResponse> listarTodas();
}
