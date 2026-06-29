/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.notification.service;

/** Genera el HTML de los correos de alerta con la identidad visual Lynx. */
public interface EmailTemplateService {

    String accesoFallido(String nombre, String ip, Object intentosRestantes);

    String spoofingDetectado(String ip, String pais, String fotoCaptura, String tipoSpoofing);

    String cuentaBloqueada(String nombre, Object minutos);

    String ipDesconocida(String nombre, String ip, String pais, String ciudad);
}
