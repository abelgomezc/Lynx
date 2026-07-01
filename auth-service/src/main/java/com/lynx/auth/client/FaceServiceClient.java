/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.client;

import com.lynx.auth.client.dto.FaceRegistroDto;
import com.lynx.auth.client.dto.FaceVerificacionDto;
import com.lynx.auth.client.dto.FaceVerificarDto;
import com.lynx.auth.client.dto.VoiceprintDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/** Cliente Feign hacia face-service (embeddings faciales y de voz en pgvector). */
@FeignClient(name = "FACE-SERVICE", url = "${face.service.url}")
public interface FaceServiceClient {

    @PostMapping("/face/register")
    FaceVerificacionDto registrarRostro(@RequestBody FaceRegistroDto request);

    @PostMapping("/face/verify")
    FaceVerificacionDto verificarRostro(@RequestBody FaceVerificarDto request);

    @PostMapping("/face/voice/register")
    void registrarVoiceprint(@RequestBody VoiceprintDto request);

    @GetMapping("/face/voice/{idUsuario}")
    VoiceprintDto obtenerVoiceprint(@PathVariable("idUsuario") Long idUsuario);

    @DeleteMapping("/face/usuario/{idUsuario}")
    void eliminarBiometria(@PathVariable("idUsuario") Long idUsuario);
}
