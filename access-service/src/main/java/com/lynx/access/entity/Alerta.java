/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.access.entity;

import com.lynx.access.config.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Alerta de seguridad generada a partir de eventos del sistema. */
@Entity
@Table(name = "alertas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alerta extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(nullable = false)
    private String descripcion;

    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "foto_evidencia")
    private String fotoEvidencia;

    @Column(nullable = false)
    @Builder.Default
    private Boolean resuelta = false;
}
