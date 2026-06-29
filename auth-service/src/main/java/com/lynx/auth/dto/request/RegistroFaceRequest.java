/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Registro facial (paso 2). Embedding de 128 dimensiones + foto opcional. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroFaceRequest {

    @NotNull(message = "El id de usuario es obligatorio")
    private Long idUsuario;

    @NotEmpty(message = "El embedding facial es obligatorio")
    private List<Double> embedding;

    /** Foto de referencia en base64 (opcional). */
    private String fotoReferencia;
}
