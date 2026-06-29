/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.face.repository;

import com.lynx.face.entity.EmbeddingFacial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Acceso a embeddings faciales con búsqueda por similitud (pgvector). */
@Repository
public interface EmbeddingFacialRepository extends JpaRepository<EmbeddingFacial, Long> {

    /** Proyección del usuario más parecido y su distancia coseno. */
    interface ResultadoBusqueda {
        Long getIdUsuario();
        Double getDistancia();
    }

    @Modifying
    @Query(value = """
            INSERT INTO embeddings_faciales (id_usuario, embedding, foto_referencia, es_activo, fecha_creacion, fecha_actualizacion)
            VALUES (:idUsuario, CAST(:embedding AS vector), :fotoReferencia, true, NOW(), NOW())
            """, nativeQuery = true)
    void insertar(@Param("idUsuario") Long idUsuario,
                  @Param("embedding") String embedding,
                  @Param("fotoReferencia") String fotoReferencia);

    @Query(value = """
            SELECT id_usuario AS idUsuario,
                   (embedding <=> CAST(:embedding AS vector)) AS distancia
            FROM embeddings_faciales
            WHERE es_activo = true
            ORDER BY embedding <=> CAST(:embedding AS vector)
            LIMIT 1
            """, nativeQuery = true)
    ResultadoBusqueda buscarMasCercano(@Param("embedding") String embedding);

    @Modifying
    @Query(value = "UPDATE embeddings_faciales SET es_activo = false WHERE id_usuario = :idUsuario", nativeQuery = true)
    void desactivarPorUsuario(@Param("idUsuario") Long idUsuario);
}
