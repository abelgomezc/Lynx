/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Factor 1: embedding facial generado por face-api.js en el navegador. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginFaceRequest {

    @NotEmpty(message = "El embedding facial es obligatorio")
    private List<Double> embedding;

    private String ipAddress;

    private String dispositivo;
}
