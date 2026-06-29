/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.access.service.impl;

import com.lynx.access.dto.response.AlertaResponse;
import com.lynx.access.entity.Alerta;
import com.lynx.access.repository.AlertaRepository;
import com.lynx.access.service.AlertaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/** Implementación de la gestión de alertas de seguridad. */
@Service
@Slf4j
public class AlertaServiceImpl implements AlertaService {

    @Autowired
    private AlertaRepository alertaRepository;

    @Override
    @Transactional
    public Alerta crear(String tipo, String descripcion, Long idUsuario,
                        String ipAddress, String fotoEvidencia) {
        Alerta alerta = Alerta.builder()
                .tipo(tipo)
                .descripcion(descripcion)
                .idUsuario(idUsuario)
                .ipAddress(ipAddress)
                .fotoEvidencia(fotoEvidencia)
                .resuelta(false)
                .build();
        alerta = alertaRepository.save(alerta);
        log.info("Alerta creada: {} - {}", tipo, descripcion);
        return alerta;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertaResponse> listarActivas() {
        return alertaRepository.findByResueltaFalseOrderByFechaCreacionDesc()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertaResponse> listarTodas() {
        return alertaRepository.findAllByOrderByFechaCreacionDesc()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private AlertaResponse toResponse(Alerta a) {
        return AlertaResponse.builder()
                .id(a.getId())
                .tipo(a.getTipo())
                .descripcion(a.getDescripcion())
                .idUsuario(a.getIdUsuario())
                .ipAddress(a.getIpAddress())
                .fotoEvidencia(a.getFotoEvidencia())
                .resuelta(a.getResuelta())
                .fechaCreacion(a.getFechaCreacion())
                .build();
    }
}
