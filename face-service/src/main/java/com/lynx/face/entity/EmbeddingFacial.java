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
 * Embedding facial de 128 dimensiones almacenado en pgvector.
 * La columna {@code embedding vector(128)} se gestiona por SQL nativo
 * (JdbcTemplate / consultas nativas) y no se mapea aquí.
 */
@Entity
@Table(name = "embeddings_faciales")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddingFacial extends BaseEntity {

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "foto_referencia")
    private String fotoReferencia;

    @Column(name = "es_activo", nullable = false)
    @Builder.Default
    private Boolean esActivo = true;
}
