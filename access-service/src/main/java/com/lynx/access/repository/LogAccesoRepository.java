/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.access.repository;

import com.lynx.access.entity.LogAcceso;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogAccesoRepository extends JpaRepository<LogAcceso, Long> {

    List<LogAcceso> findByIdUsuarioOrderByFechaCreacionDesc(Long idUsuario, Pageable pageable);

    List<LogAcceso> findAllByOrderByFechaCreacionDesc(Pageable pageable);

    long countByResultadoAndFechaCreacionAfter(String resultado, LocalDateTime desde);

    long countByFechaCreacionAfter(LocalDateTime desde);

    @Query("SELECT COUNT(DISTINCT l.ipAddress) FROM LogAcceso l WHERE l.ipAddress = :ip AND l.idUsuario = :idUsuario")
    long contarIpConocida(@Param("ip") String ip, @Param("idUsuario") Long idUsuario);

    boolean existsByIdUsuarioAndIpAddress(Long idUsuario, String ipAddress);
}
