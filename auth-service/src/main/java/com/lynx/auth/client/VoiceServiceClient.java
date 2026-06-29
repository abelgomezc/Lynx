/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.client;

import com.lynx.auth.client.dto.VoiceRegistroDto;
import com.lynx.auth.client.dto.VoiceVerificacionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * Cliente Feign hacia voice-service (Python FastAPI).
 * Envía TODO como multipart (campos incluidos) usando feign-form-spring:
 * FastAPI espera frase_esperada / id_usuario como campos Form, no como
 * parámetros de URL. Por eso se usan @RequestPart en todos los campos.
 */
@FeignClient(name = "voice-service", url = "${voice.service.url}")
public interface VoiceServiceClient {

    @PostMapping(value = "/voice/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    VoiceRegistroDto registrarVoz(
            @RequestPart("audio") MultipartFile audio,
            @RequestPart("frase_esperada") String fraseEsperada,
            @RequestPart("id_usuario") String idUsuario);

    @PostMapping(value = "/voice/verify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    VoiceVerificacionDto verificarVoz(
            @RequestPart("audio") MultipartFile audio,
            @RequestPart("frase_esperada") String fraseEsperada,
            @RequestPart("voiceprint_registrado") String voiceprintRegistrado);
}
