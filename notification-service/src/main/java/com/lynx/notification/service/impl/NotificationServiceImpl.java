/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.notification.service.impl;

import com.lynx.notification.service.EmailTemplateService;
import com.lynx.notification.service.NotificationService;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;

/** Implementación del envío de correos de alerta de Lynx. */
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailTemplateService templates;

    @Value("${spring.mail.username}")
    private String remitente;

    @Value("${lynx.admin.email}")
    private String adminEmail;

    @Override
    public void notificarAccesoFallido(Map<String, Object> evento) {
        String destino = str(evento.get("email"));
        String html = templates.accesoFallido(str(evento.get("email")),
                str(evento.get("ipAddress")), evento.get("intentosRestantes"));
        enviar(destino, "Lynx · Intento de acceso fallido", html);
    }

    @Override
    public void notificarSpoofing(Map<String, Object> evento) {
        String html = templates.spoofingDetectado(
                str(evento.get("ipAddress")), str(evento.get("pais")),
                str(evento.get("fotoCaptura")), str(evento.get("tipoSpoofing")));
        // Las alertas de spoofing van al administrador
        enviar(adminEmail, "Lynx · ALERTA: Spoofing detectado", html);
    }

    @Override
    public void notificarCuentaBloqueada(Map<String, Object> evento) {
        String html = templates.cuentaBloqueada(str(evento.get("nombreUsuario")),
                evento.get("minutosBloqueo"));
        // Email al usuario y al administrador
        enviar(str(evento.get("email")), "Lynx · Tu cuenta está bloqueada", html);
        enviar(adminEmail, "Lynx · Usuario bloqueado: " + str(evento.get("email")), html);
    }

    @Override
    public void notificarIpDesconocida(Map<String, Object> evento) {
        String html = templates.ipDesconocida(str(evento.get("email")),
                str(evento.get("ipAddress")), str(evento.get("pais")), str(evento.get("ciudad")));
        enviar(str(evento.get("email")), "Lynx · Acceso desde IP desconocida", html);
        enviar(adminEmail, "Lynx · IP sospechosa detectada", html);
    }

    // ---------------------------------------------------------------------

    private void enviar(String destinatario, String asunto, String html) {
        if (destinatario == null || destinatario.isBlank()) {
            log.warn("No se envía '{}': destinatario vacío", asunto);
            return;
        }
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
            helper.setFrom(remitente);
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(html, true);
            mailSender.send(mensaje);
            log.info("Correo enviado a {} · {}", destinatario, asunto);
        } catch (Exception ex) {
            // En desarrollo local sin SMTP real, se registra y se continúa
            log.error("No se pudo enviar correo a {} ('{}'): {}", destinatario, asunto, ex.getMessage());
        }
    }

    private String str(Object o) {
        return o != null ? o.toString() : null;
    }
}
