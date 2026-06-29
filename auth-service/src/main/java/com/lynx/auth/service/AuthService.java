/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.service;

import com.lynx.auth.dto.request.LoginFaceRequest;
import com.lynx.auth.dto.request.LoginVoiceRequest;
import com.lynx.auth.dto.request.RefreshRequest;
import com.lynx.auth.dto.request.RegistroFaceRequest;
import com.lynx.auth.dto.request.RegistroRequest;
import com.lynx.auth.dto.response.AuthResponse;
import com.lynx.auth.dto.response.UsuarioResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/** Lógica de registro, autenticación biométrica dual y gestión de tokens. */
public interface AuthService {

    UsuarioResponse registrar(RegistroRequest request);

    UsuarioResponse registrarRostro(RegistroFaceRequest request);

    UsuarioResponse registrarVoz(MultipartFile audio, String fraseEsperada, Long idUsuario);

    /** Factor 1: verifica el rostro y devuelve la frase para el factor de voz. */
    AuthResponse loginFacial(LoginFaceRequest request);

    /** Factor 2: verifica la voz y, si todo es correcto, emite los tokens. */
    AuthResponse loginVoz(MultipartFile audio, LoginVoiceRequest request);

    AuthResponse refresh(RefreshRequest request);

    void logout(RefreshRequest request);

    UsuarioResponse obtenerActual(Long idUsuario);

    /** Genera (o recupera) una frase aleatoria anti-replay para un usuario. */
    String generarFrase(Long idUsuario);

    // ----- Administración (rol ADMIN) -----

    List<UsuarioResponse> listarUsuarios();

    UsuarioResponse bloquearUsuario(Long idUsuario);

    UsuarioResponse desbloquearUsuario(Long idUsuario);
}
