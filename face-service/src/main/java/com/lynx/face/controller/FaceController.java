/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.face.controller;

import com.lynx.face.dto.request.RegistroFaceRequest;
import com.lynx.face.dto.request.VerificarFaceRequest;
import com.lynx.face.dto.request.VoiceprintRequest;
import com.lynx.face.dto.response.FaceVerificacionResponse;
import com.lynx.face.dto.response.VoiceprintResponse;
import com.lynx.face.service.FaceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Endpoints de registro y verificación facial y de voiceprints. */
@RestController
@RequestMapping("/face")
public class FaceController {

    @Autowired
    private FaceService faceService;

    @PostMapping("/register")
    public ResponseEntity<FaceVerificacionResponse> registrar(@Valid @RequestBody RegistroFaceRequest request) {
        return ResponseEntity.ok(faceService.registrar(request));
    }

    @PostMapping("/verify")
    public ResponseEntity<FaceVerificacionResponse> verificar(@Valid @RequestBody VerificarFaceRequest request) {
        return ResponseEntity.ok(faceService.verificar(request));
    }

    @PostMapping("/voice/register")
    public ResponseEntity<Void> registrarVoiceprint(@Valid @RequestBody VoiceprintRequest request) {
        faceService.registrarVoiceprint(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/voice/{idUsuario}")
    public ResponseEntity<VoiceprintResponse> obtenerVoiceprint(@PathVariable("idUsuario") Long idUsuario) {
        return ResponseEntity.ok(faceService.obtenerVoiceprint(idUsuario));
    }

    @DeleteMapping("/usuario/{idUsuario}")
    public ResponseEntity<Void> eliminarBiometria(@PathVariable("idUsuario") Long idUsuario) {
        faceService.eliminarBiometria(idUsuario);
        return ResponseEntity.noContent().build();
    }
}
