/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.face.service.impl;

import com.lynx.face.dto.request.MuestraLivenessDto;
import com.lynx.face.dto.request.RegistroFaceRequest;
import com.lynx.face.dto.request.VerificarFaceRequest;
import com.lynx.face.dto.request.VoiceprintRequest;
import com.lynx.face.dto.response.FaceVerificacionResponse;
import com.lynx.face.dto.response.VoiceprintResponse;
import com.lynx.face.exception.EmbeddingFacialException;
import com.lynx.face.exception.SpoofingDetectadoException;
import com.lynx.face.kafka.FaceEventProducer;
import com.lynx.face.repository.EmbeddingFacialRepository;
import com.lynx.face.repository.EmbeddingVozRepository;
import com.lynx.face.service.FaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** Implementación de la lógica facial y de voz con pgvector. */
@Service
@Slf4j
public class FaceServiceImpl implements FaceService {

    @Autowired
    private EmbeddingFacialRepository embeddingFacialRepository;

    @Autowired
    private EmbeddingVozRepository embeddingVozRepository;

    @Autowired
    private FaceEventProducer faceEventProducer;

    @Value("${face.similitud.umbral}")
    private double umbralFacial;

    @Value("${face.embedding.dimensions}")
    private int dimensiones;

    @Override
    @Transactional
    public FaceVerificacionResponse registrar(RegistroFaceRequest request) {
        validarDimensiones(request.getEmbedding());
        embeddingFacialRepository.desactivarPorUsuario(request.getIdUsuario());
        embeddingFacialRepository.insertar(
                request.getIdUsuario(),
                aVectorString(request.getEmbedding()),
                request.getFotoReferencia());
        log.info("Embedding facial registrado para usuario {}", request.getIdUsuario());
        return FaceVerificacionResponse.builder()
                .idUsuario(request.getIdUsuario())
                .reconocido(true)
                .confianza(1.0)
                .distancia(0.0)
                .esSpoofing(false)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public FaceVerificacionResponse verificar(VerificarFaceRequest request) {
        validarDimensiones(request.getEmbedding());

        // Anti-spoofing: el SERVIDOR verifica la prueba de vida a partir de la
        // serie de métricas (no se confía en ningún flag del cliente). Una foto
        // estática no puede parpadear, abrir la boca ni girar la cabeza.
        verificarLiveness(request.getAccionLiveness(), request.getMuestrasLiveness(),
                request.getIpAddress(), request.getFotoCaptura());

        EmbeddingFacialRepository.ResultadoBusqueda resultado =
                embeddingFacialRepository.buscarMasCercano(aVectorString(request.getEmbedding()));

        if (resultado == null || resultado.getIdUsuario() == null) {
            return FaceVerificacionResponse.builder()
                    .reconocido(false).confianza(0.0).distancia(1.0).esSpoofing(false)
                    .build();
        }

        double distancia = resultado.getDistancia();
        boolean reconocido = distancia < umbralFacial;
        double confianza = Math.max(0.0, 1.0 - distancia);

        log.info("Verificación facial: usuario={}, distancia={}, reconocido={}",
                resultado.getIdUsuario(), distancia, reconocido);

        return FaceVerificacionResponse.builder()
                .idUsuario(reconocido ? resultado.getIdUsuario() : null)
                .reconocido(reconocido)
                .confianza(redondear(confianza))
                .distancia(redondear(distancia))
                .esSpoofing(false)
                .build();
    }

    @Override
    @Transactional
    public void registrarVoiceprint(VoiceprintRequest request) {
        embeddingVozRepository.desactivarPorUsuario(request.getIdUsuario());
        embeddingVozRepository.insertar(request.getIdUsuario(), aVectorString(request.getVoiceprint()));
        log.info("Voiceprint registrado para usuario {}", request.getIdUsuario());
    }

    @Override
    @Transactional(readOnly = true)
    public VoiceprintResponse obtenerVoiceprint(Long idUsuario) {
        String raw = embeddingVozRepository.obtenerVoiceprint(idUsuario);
        if (raw == null) {
            throw new EmbeddingFacialException("No hay voiceprint registrado para el usuario " + idUsuario);
        }
        return VoiceprintResponse.builder()
                .idUsuario(idUsuario)
                .voiceprint(parsearVector(raw))
                .build();
    }

    @Override
    @Transactional
    public void eliminarBiometria(Long idUsuario) {
        embeddingFacialRepository.eliminarPorUsuario(idUsuario);
        embeddingVozRepository.eliminarPorUsuario(idUsuario);
        log.info("Biometría eliminada para usuario {} (compensación)", idUsuario);
    }

    // ---------------------------------------------------------------------

    /**
     * Verifica la prueba de vida en el SERVIDOR: comprueba que la acción
     * solicitada realmente ocurrió analizando la serie de métricas.
     * Una foto o pantalla no puede parpadear, abrir la boca ni girar.
     */
    private void verificarLiveness(String accion, List<MuestraLivenessDto> muestras,
                                   String ip, String foto) {
        if (accion == null || muestras == null || muestras.size() < 5) {
            faceEventProducer.publicarSpoofing(ip, "Desconocido", foto, "SIN_PRUEBA_VIDA");
            throw new SpoofingDetectadoException(
                    "Prueba de vida insuficiente. Realiza la acción indicada frente a la cámara.");
        }

        double earMin = Double.MAX_VALUE, earMax = -Double.MAX_VALUE;
        double marMin = Double.MAX_VALUE, marMax = -Double.MAX_VALUE;
        double yawMin = Double.MAX_VALUE, yawMax = -Double.MAX_VALUE;
        for (MuestraLivenessDto m : muestras) {
            if (m.getEar() != null) { earMin = Math.min(earMin, m.getEar()); earMax = Math.max(earMax, m.getEar()); }
            if (m.getMar() != null) { marMin = Math.min(marMin, m.getMar()); marMax = Math.max(marMax, m.getMar()); }
            if (m.getYaw() != null) { yawMin = Math.min(yawMin, m.getYaw()); yawMax = Math.max(yawMax, m.getYaw()); }
        }

        boolean ok;
        switch (accion) {
            case "PARPADEA" -> ok = earMin < 0.20 && earMax > 0.27;      // ojo se cierra y abre
            case "ABRE_BOCA" -> ok = marMax > 0.45 && (marMax - marMin) > 0.20;
            case "GIRA_CABEZA" -> ok = (yawMax - yawMin) > 0.22;         // la cabeza gira
            default -> ok = false;
        }

        log.info("Liveness accion={} ok={} earMin={} earMax={} marRange={} yawRange={}",
                accion, ok, redondear(earMin), redondear(earMax),
                redondear(marMax - marMin), redondear(yawMax - yawMin));

        if (!ok) {
            faceEventProducer.publicarSpoofing(ip, "Desconocido", foto, "ACCION_NO_DETECTADA:" + accion);
            throw new SpoofingDetectadoException(
                    "No detectamos la prueba de vida solicitada. Posible foto o video. Intenta de nuevo.");
        }
    }

    private void validarDimensiones(List<Double> embedding) {
        if (embedding == null || embedding.isEmpty()) {
            throw new EmbeddingFacialException("El embedding no puede estar vacío");
        }
    }

    /** Convierte una lista de doubles al literal de pgvector "[a,b,c]". */
    private String aVectorString(List<Double> valores) {
        return valores.stream().map(String::valueOf)
                .collect(Collectors.joining(",", "[", "]"));
    }

    /** Parsea el literal "[a,b,c]" de pgvector a lista de doubles. */
    private List<Double> parsearVector(String raw) {
        String limpio = raw.replace("[", "").replace("]", "").trim();
        List<Double> valores = new ArrayList<>();
        if (limpio.isEmpty()) {
            return valores;
        }
        for (String parte : limpio.split(",")) {
            valores.add(Double.parseDouble(parte.trim()));
        }
        return valores;
    }

    private double redondear(double valor) {
        return Math.round(valor * 10000.0) / 10000.0;
    }
}
