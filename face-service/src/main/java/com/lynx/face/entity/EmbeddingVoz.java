/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.face.entity;

import com.lynx.face.config.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Voiceprint (MFCC de 13 componentes) almacenado en pgvector.
 * La columna {@code mfcc_features vector(13)} se gestiona por SQL nativo.
 */
@Entity
@Table(name = "embeddings_voz")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddingVoz extends BaseEntity {

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "es_activo", nullable = false)
    @Builder.Default
    private Boolean esActivo = true;
}
