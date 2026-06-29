/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.entity;

import com.lynx.auth.config.BaseEntity;
import com.lynx.auth.enums.EstadoRegistro;
import com.lynx.auth.enums.Rol;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Usuario del sistema. No tiene contraseña: la identidad es biométrica. */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario extends BaseEntity {

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Rol rol = Rol.EMPLEADO;

    private String departamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_registro", nullable = false, length = 30)
    @Builder.Default
    private EstadoRegistro estadoRegistro = EstadoRegistro.PENDIENTE_BIOMETRIA;

    @Column(name = "es_activo", nullable = false)
    @Builder.Default
    private Boolean esActivo = true;

    @Column(name = "intentos_fallidos", nullable = false)
    @Builder.Default
    private Integer intentosFallidos = 0;

    @Column(name = "bloqueado_hasta")
    private LocalDateTime bloqueadoHasta;
}
