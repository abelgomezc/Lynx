/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lynx.auth.dto.request.LoginFaceRequest;
import com.lynx.auth.dto.request.LoginVoiceRequest;
import com.lynx.auth.dto.request.RefreshRequest;
import com.lynx.auth.dto.request.RegistroFaceRequest;
import com.lynx.auth.dto.request.RegistroRequest;
import com.lynx.auth.dto.response.AuthResponse;
import com.lynx.auth.dto.response.UsuarioResponse;
import com.lynx.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/** Endpoints de registro, autenticación biométrica dual y gestión de tokens. */
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(request));
    }

    @PostMapping("/register/face")
    public ResponseEntity<UsuarioResponse> registrarRostro(@Valid @RequestBody RegistroFaceRequest request) {
        return ResponseEntity.ok(authService.registrarRostro(request));
    }

    @PostMapping(value = "/register/voice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UsuarioResponse> registrarVoz(
            @RequestParam("audio") MultipartFile audio,
            @RequestParam("fraseEsperada") String fraseEsperada,
            @RequestParam("idUsuario") Long idUsuario) {
        return ResponseEntity.ok(authService.registrarVoz(audio, fraseEsperada, idUsuario));
    }

    /** Registro ATÓMICO: crea el usuario y guarda cara + voz en una sola operación. */
    @PostMapping(value = "/register/complete", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UsuarioResponse> registrarCompleto(
            @RequestParam("nombre") String nombre,
            @RequestParam("email") String email,
            @RequestParam(value = "departamento", required = false) String departamento,
            @RequestParam("embedding") String embeddingJson,
            @RequestParam(value = "fotoReferencia", required = false) String fotoReferencia,
            @RequestParam("audio") MultipartFile audio,
            @RequestParam("fraseEsperada") String fraseEsperada) throws Exception {
        List<Double> embedding = objectMapper.readValue(embeddingJson, new TypeReference<List<Double>>() {});
        RegistroRequest datos = RegistroRequest.builder()
                .nombre(nombre)
                .email(email)
                .departamento(departamento)
                .rol("EMPLEADO")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.registrarCompleto(datos, embedding, fotoReferencia, audio, fraseEsperada));
    }

    @GetMapping("/register/frase")
    public ResponseEntity<Map<String, String>> fraseRegistro() {
        return ResponseEntity.ok(Map.of("frase", authService.fraseRegistro()));
    }

    @GetMapping("/register/disponible")
    public ResponseEntity<Map<String, Boolean>> emailDisponible(@RequestParam("email") String email) {
        return ResponseEntity.ok(Map.of("disponible", authService.emailDisponible(email)));
    }

    @PostMapping("/login/face")
    public ResponseEntity<AuthResponse> loginFacial(@Valid @RequestBody LoginFaceRequest request) {
        return ResponseEntity.ok(authService.loginFacial(request));
    }

    @PostMapping(value = "/login/voice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AuthResponse> loginVoz(
            @RequestParam("audio") MultipartFile audio,
            @RequestParam("idUsuario") Long idUsuario,
            @RequestParam(value = "fraseEsperada", required = false) String fraseEsperada,
            @RequestParam(value = "confianzaFacial", required = false) Double confianzaFacial,
            @RequestParam(value = "ipAddress", required = false) String ipAddress,
            @RequestParam(value = "dispositivo", required = false) String dispositivo) {
        LoginVoiceRequest request = LoginVoiceRequest.builder()
                .idUsuario(idUsuario)
                .fraseEsperada(fraseEsperada)
                .confianzaFacial(confianzaFacial)
                .ipAddress(ipAddress)
                .dispositivo(dispositivo)
                .build();
        return ResponseEntity.ok(authService.loginVoz(audio, request));
    }

    @GetMapping("/frase")
    public ResponseEntity<Map<String, String>> generarFrase(@RequestParam("idUsuario") Long idUsuario) {
        return ResponseEntity.ok(Map.of("frase", authService.generarFrase(idUsuario)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me(@RequestHeader("X-Usuario-Id") Long idUsuario) {
        return ResponseEntity.ok(authService.obtenerActual(idUsuario));
    }
}
