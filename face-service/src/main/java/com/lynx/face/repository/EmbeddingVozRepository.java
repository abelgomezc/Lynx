/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.face.repository;

import com.lynx.face.entity.EmbeddingVoz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Acceso a voiceprints (MFCC) almacenados en pgvector. */
@Repository
public interface EmbeddingVozRepository extends JpaRepository<EmbeddingVoz, Long> {

    @Modifying
    @Query(value = """
            INSERT INTO embeddings_voz (id_usuario, mfcc_features, es_activo, fecha_creacion, fecha_actualizacion)
            VALUES (:idUsuario, CAST(:mfcc AS vector), true, NOW(), NOW())
            """, nativeQuery = true)
    void insertar(@Param("idUsuario") Long idUsuario, @Param("mfcc") String mfcc);

    /** Devuelve el voiceprint activo más reciente como texto "[v1,v2,...]". */
    @Query(value = """
            SELECT mfcc_features::text
            FROM embeddings_voz
            WHERE id_usuario = :idUsuario AND es_activo = true
            ORDER BY fecha_creacion DESC
            LIMIT 1
            """, nativeQuery = true)
    String obtenerVoiceprint(@Param("idUsuario") Long idUsuario);

    @Modifying
    @Query(value = "UPDATE embeddings_voz SET es_activo = false WHERE id_usuario = :idUsuario", nativeQuery = true)
    void desactivarPorUsuario(@Param("idUsuario") Long idUsuario);
}
