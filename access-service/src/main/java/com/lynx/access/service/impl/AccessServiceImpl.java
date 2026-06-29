/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.access.service.impl;

import com.lynx.access.config.KafkaConfig;
import com.lynx.access.controller.AccessWebSocketController;
import com.lynx.access.dto.response.LogAccesoResponse;
import com.lynx.access.dto.response.MetricasResponse;
import com.lynx.access.entity.LogAcceso;
import com.lynx.access.enums.ResultadoAcceso;
import com.lynx.access.enums.TipoAlerta;
import com.lynx.access.repository.AlertaRepository;
import com.lynx.access.repository.LogAccesoRepository;
import com.lynx.access.service.AccessService;
import com.lynx.access.service.AlertaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Implementación del registro y consulta de accesos y métricas. */
@Service
@Slf4j
public class AccessServiceImpl implements AccessService {

    @Autowired
    private LogAccesoRepository logAccesoRepository;

    @Autowired
    private AlertaRepository alertaRepository;

    @Autowired
    private AlertaService alertaService;

    @Autowired
    private AccessWebSocketController webSocket;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${ip.geolocation.url}")
    private String geolocationUrl;

    private final RestClient restClient = RestClient.create();

    @Override
    @Transactional
    public void procesarAccesoExitoso(Map<String, Object> evento) {
        String ip = str(evento.get("ipAddress"));
        Long idUsuario = lng(evento.get("idUsuario"));
        Map<String, String> geo = geolocalizar(ip);

        // Detección de IP desconocida para este usuario (antes de guardar)
        boolean ipConocida = idUsuario != null
                && logAccesoRepository.existsByIdUsuarioAndIpAddress(idUsuario, ip);

        LogAcceso log = LogAcceso.builder()
                .idUsuario(idUsuario)
                .nombreUsuario(str(evento.get("nombreUsuario")))
                .ipAddress(ip)
                .pais(geo.get("pais"))
                .ciudad(geo.get("ciudad"))
                .dispositivo(str(evento.get("dispositivo")))
                .resultado(ResultadoAcceso.EXITOSO.name())
                .factor1Exitoso(true)
                .factor2Exitoso(true)
                .confianzaFacial(dec(evento.get("confianzaFacial")))
                .confianzaVoz(dec(evento.get("confianzaVoz")))
                .esSpoofing(false)
                .build();
        log = logAccesoRepository.save(log);
        difundir(log);

        if (!ipConocida && idUsuario != null) {
            alertaService.crear(TipoAlerta.IP_DESCONOCIDA.name(),
                    "Acceso desde una IP no usada antes: " + ip + " (" + geo.get("pais") + ")",
                    idUsuario, ip, null);
            publicarIpSospechosa(idUsuario, str(evento.get("email")), ip, geo);
        }
    }

    @Override
    @Transactional
    public void procesarAccesoFallido(Map<String, Object> evento) {
        String ip = str(evento.get("ipAddress"));
        Long idUsuario = lng(evento.get("idUsuario"));
        Map<String, String> geo = geolocalizar(ip);

        LogAcceso log = LogAcceso.builder()
                .idUsuario(idUsuario)
                .nombreUsuario(str(evento.get("email")))
                .ipAddress(ip != null ? ip : "desconocida")
                .pais(geo.get("pais"))
                .ciudad(geo.get("ciudad"))
                .resultado(ResultadoAcceso.FALLIDO.name())
                .factorFallido(str(evento.get("factorFallido")))
                .factor1Exitoso(false)
                .factor2Exitoso(false)
                .esSpoofing(false)
                .build();
        log = logAccesoRepository.save(log);
        difundir(log);

        alertaService.crear(TipoAlerta.ACCESO_FALLIDO.name(),
                "Intento de acceso fallido. Factor: " + str(evento.get("factorFallido")),
                idUsuario, ip, null);
    }

    @Override
    @Transactional
    public void procesarSpoofing(Map<String, Object> evento) {
        String ip = str(evento.get("ipAddress"));
        String foto = str(evento.get("fotoCaptura"));

        LogAcceso log = LogAcceso.builder()
                .ipAddress(ip != null ? ip : "desconocida")
                .pais(str(evento.get("pais")))
                .resultado(ResultadoAcceso.SPOOFING.name())
                .factor1Exitoso(false)
                .fotoCaptura(foto)
                .esSpoofing(true)
                .build();
        log = logAccesoRepository.save(log);
        difundir(log);

        alertaService.crear(TipoAlerta.SPOOFING_DETECTADO.name(),
                "Intento de suplantación detectado: " + str(evento.get("tipoSpoofing")),
                null, ip, foto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LogAccesoResponse> historialUsuario(Long idUsuario) {
        Pageable top = PageRequest.of(0, 50);
        return logAccesoRepository.findByIdUsuarioOrderByFechaCreacionDesc(idUsuario, top)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LogAccesoResponse> todos() {
        Pageable top = PageRequest.of(0, 100);
        return logAccesoRepository.findAllByOrderByFechaCreacionDesc(top)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MetricasResponse metricas() {
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        long total = logAccesoRepository.countByFechaCreacionAfter(inicioDia);
        long exitosos = logAccesoRepository.countByResultadoAndFechaCreacionAfter(
                ResultadoAcceso.EXITOSO.name(), inicioDia);
        long fallidos = logAccesoRepository.countByResultadoAndFechaCreacionAfter(
                ResultadoAcceso.FALLIDO.name(), inicioDia);
        long spoofing = logAccesoRepository.countByResultadoAndFechaCreacionAfter(
                ResultadoAcceso.SPOOFING.name(), inicioDia);
        long alertas = alertaRepository.countByResueltaFalse();

        return MetricasResponse.builder()
                .accesosHoy(total)
                .exitososHoy(exitosos)
                .fallidosHoy(fallidos)
                .spoofingHoy(spoofing)
                .alertasActivas(alertas)
                .build();
    }

    // ---------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private Map<String, String> geolocalizar(String ip) {
        Map<String, String> resultado = new HashMap<>();
        resultado.put("pais", "Desconocido");
        resultado.put("ciudad", "Desconocida");
        if (ip == null || ip.isBlank() || ip.startsWith("192.168.")
                || ip.startsWith("10.") || ip.startsWith("127.") || "localhost".equals(ip)) {
            resultado.put("pais", "Red local");
            resultado.put("ciudad", "Local");
            return resultado;
        }
        try {
            Map<String, Object> respuesta = restClient.get()
                    .uri(geolocationUrl + ip)
                    .retrieve()
                    .body(Map.class);
            if (respuesta != null && "success".equals(respuesta.get("status"))) {
                resultado.put("pais", str(respuesta.get("country")));
                resultado.put("ciudad", str(respuesta.get("city")));
            }
        } catch (Exception ex) {
            log.warn("No se pudo geolocalizar IP {}: {}", ip, ex.getMessage());
        }
        return resultado;
    }

    private void publicarIpSospechosa(Long idUsuario, String email, String ip, Map<String, String> geo) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("idUsuario", idUsuario);
        payload.put("email", email);
        payload.put("ipAddress", ip);
        payload.put("pais", geo.get("pais"));
        payload.put("ciudad", geo.get("ciudad"));
        payload.put("timestamp", LocalDateTime.now().toString());
        try {
            kafkaTemplate.send(KafkaConfig.TOPIC_IP_SOSPECHOSA, payload);
        } catch (Exception ex) {
            log.error("No se pudo publicar ip.sospechosa: {}", ex.getMessage());
        }
    }

    private void difundir(LogAcceso log) {
        webSocket.difundir(toResponse(log));
    }

    private LogAccesoResponse toResponse(LogAcceso l) {
        return LogAccesoResponse.builder()
                .id(l.getId())
                .idUsuario(l.getIdUsuario())
                .nombreUsuario(l.getNombreUsuario())
                .ipAddress(l.getIpAddress())
                .pais(l.getPais())
                .ciudad(l.getCiudad())
                .dispositivo(l.getDispositivo())
                .resultado(l.getResultado())
                .factor1Exitoso(l.getFactor1Exitoso())
                .factor2Exitoso(l.getFactor2Exitoso())
                .factorFallido(l.getFactorFallido())
                .confianzaFacial(l.getConfianzaFacial())
                .confianzaVoz(l.getConfianzaVoz())
                .esSpoofing(l.getEsSpoofing())
                .fechaCreacion(l.getFechaCreacion())
                .build();
    }

    private String str(Object o) {
        return o != null ? o.toString() : null;
    }

    private Long lng(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Number n) {
            return n.longValue();
        }
        try {
            return Long.valueOf(o.toString());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private BigDecimal dec(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Number n) {
            return BigDecimal.valueOf(n.doubleValue());
        }
        try {
            return new BigDecimal(o.toString());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
