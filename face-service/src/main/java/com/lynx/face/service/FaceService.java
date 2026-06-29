/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.face.service;

import com.lynx.face.dto.request.RegistroFaceRequest;
import com.lynx.face.dto.request.VerificarFaceRequest;
import com.lynx.face.dto.request.VoiceprintRequest;
import com.lynx.face.dto.response.FaceVerificacionResponse;
import com.lynx.face.dto.response.VoiceprintResponse;

/** Registro y verificación de embeddings faciales y de voz en pgvector. */
public interface FaceService {

    FaceVerificacionResponse registrar(RegistroFaceRequest request);

    FaceVerificacionResponse verificar(VerificarFaceRequest request);

    void registrarVoiceprint(VoiceprintRequest request);

    VoiceprintResponse obtenerVoiceprint(Long idUsuario);
}
