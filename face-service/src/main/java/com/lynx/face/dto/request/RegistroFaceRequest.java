/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.face.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Registro de un embedding facial de 128 dimensiones. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroFaceRequest {

    @NotNull(message = "El id de usuario es obligatorio")
    private Long idUsuario;

    @NotEmpty(message = "El embedding es obligatorio")
    private List<Double> embedding;

    private String fotoReferencia;
}
