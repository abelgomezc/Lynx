/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.access.controller;

import com.lynx.access.dto.response.AlertaResponse;
import com.lynx.access.dto.response.LogAccesoResponse;
import com.lynx.access.dto.response.MetricasResponse;
import com.lynx.access.service.AccessService;
import com.lynx.access.service.AlertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Endpoints REST de historial de accesos, métricas y alertas. */
@RestController
@RequestMapping("/access")
public class AccessController {

    @Autowired
    private AccessService accessService;

    @Autowired
    private AlertaService alertaService;

    @GetMapping("/my-history")
    public ResponseEntity<List<LogAccesoResponse>> miHistorial(
            @RequestHeader("X-Usuario-Id") Long idUsuario) {
        return ResponseEntity.ok(accessService.historialUsuario(idUsuario));
    }

    @GetMapping("/all")
    public ResponseEntity<List<LogAccesoResponse>> todos() {
        return ResponseEntity.ok(accessService.todos());
    }

    @GetMapping("/metricas")
    public ResponseEntity<MetricasResponse> metricas() {
        return ResponseEntity.ok(accessService.metricas());
    }

    @GetMapping("/alertas")
    public ResponseEntity<List<AlertaResponse>> alertas() {
        return ResponseEntity.ok(alertaService.listarActivas());
    }
}
