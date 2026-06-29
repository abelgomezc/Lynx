/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.repository;

import com.lynx.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenAndEsValidoTrue(String token);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.esValido = false WHERE r.idUsuario = :idUsuario")
    void invalidarTodosDelUsuario(@Param("idUsuario") Long idUsuario);
}
