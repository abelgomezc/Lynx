/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.access.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Registro histórico de un intento de acceso. */
@Entity
@Table(name = "logs_acceso")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogAcceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "nombre_usuario")
    private String nombreUsuario;

    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    private String pais;
    private String ciudad;
    private String dispositivo;

    @Column(nullable = false, length = 20)
    private String resultado;

    @Column(name = "factor1_exitoso")
    private Boolean factor1Exitoso;

    @Column(name = "factor2_exitoso")
    private Boolean factor2Exitoso;

    @Column(name = "factor_fallido", length = 20)
    private String factorFallido;

    @Column(name = "confianza_facial")
    private BigDecimal confianzaFacial;

    @Column(name = "confianza_voz")
    private BigDecimal confianzaVoz;

    @Column(name = "foto_captura")
    private String fotoCaptura;

    @Column(name = "es_spoofing", nullable = false)
    @Builder.Default
    private Boolean esSpoofing = false;

    @CreatedDate
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;
}
