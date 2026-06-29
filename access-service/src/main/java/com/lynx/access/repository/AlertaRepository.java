/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.access.repository;

import com.lynx.access.entity.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    List<Alerta> findByResueltaFalseOrderByFechaCreacionDesc();

    List<Alerta> findAllByOrderByFechaCreacionDesc();

    long countByResueltaFalse();
}
