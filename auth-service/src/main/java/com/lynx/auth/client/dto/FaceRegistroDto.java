/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Petición de registro facial enviada a face-service. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaceRegistroDto {
    private Long idUsuario;
    private List<Double> embedding;
    private String fotoReferencia;
}
