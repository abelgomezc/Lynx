/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.notification.service.impl;

import com.lynx.notification.service.EmailTemplateService;
import org.springframework.stereotype.Service;

/** Plantillas HTML con la paleta Lynx (morado #6C63FF, fondo #0A0A14). */
@Service
public class EmailTemplateServiceImpl implements EmailTemplateService {

    private static final String PRIMARIO = "#6C63FF";
    private static final String CYAN = "#00D4AA";
    private static final String ERROR = "#FF4A4A";
    private static final String AMBER = "#EF9F27";
    private static final String FONDO = "#0A0A14";
    private static final String SUPERFICIE = "#12121E";
    private static final String TEXTO = "#E8E8F8";

    @Override
    public String accesoFallido(String nombre, String ip, Object intentosRestantes) {
        String cuerpo = """
                <p>Hola <strong>%s</strong>,</p>
                <p>Se detectó un <strong style="color:%s;">intento de acceso fallido</strong>
                a tu cuenta de Lynx.</p>
                %s
                <p>Intentos restantes antes del bloqueo: <strong>%s</strong></p>
                <p>Si no fuiste tú, te recomendamos estar atento a tu próximo inicio de sesión.</p>
                """.formatted(safe(nombre), ERROR, filaDato("Dirección IP", safe(ip)),
                String.valueOf(intentosRestantes));
        return envoltorio("Intento de acceso fallido", AMBER, cuerpo);
    }

    @Override
    public String spoofingDetectado(String ip, String pais, String fotoCaptura, String tipoSpoofing) {
        String imagen = (fotoCaptura != null && !fotoCaptura.isBlank())
                ? "<p><strong>Evidencia capturada:</strong></p>"
                  + "<img src=\"" + fotoCaptura + "\" alt=\"Captura del intento\" "
                  + "style=\"max-width:100%;border-radius:12px;border:1px solid " + PRIMARIO + ";\"/>"
                : "<p><em>No se adjuntó imagen del intento.</em></p>";
        String cuerpo = """
                <p>Se detectó un <strong style="color:%s;">intento de suplantación (spoofing)</strong>
                en el sistema Lynx.</p>
                %s
                %s
                %s
                %s
                """.formatted(ERROR,
                filaDato("Tipo", safe(tipoSpoofing)),
                filaDato("Dirección IP", safe(ip)),
                filaDato("País", safe(pais)),
                imagen);
        return envoltorio("Spoofing detectado", ERROR, cuerpo);
    }

    @Override
    public String cuentaBloqueada(String nombre, Object minutos) {
        String cuerpo = """
                <p>Hola <strong>%s</strong>,</p>
                <p>Tu cuenta de Lynx ha sido <strong style="color:%s;">bloqueada temporalmente</strong>
                por superar el número de intentos fallidos permitidos.</p>
                %s
                <p>Podrás volver a iniciar sesión con tu biometría cuando termine el bloqueo.</p>
                """.formatted(safe(nombre), ERROR,
                filaDato("Tiempo de bloqueo", minutos + " minutos"));
        return envoltorio("Cuenta bloqueada", ERROR, cuerpo);
    }

    @Override
    public String ipDesconocida(String nombre, String ip, String pais, String ciudad) {
        String cuerpo = """
                <p>Hola <strong>%s</strong>,</p>
                <p>Se registró un acceso a tu cuenta de Lynx desde una
                <strong style="color:%s;">ubicación o IP no habitual</strong>.</p>
                %s
                %s
                %s
                <p>Si reconoces este acceso, puedes ignorar este mensaje.</p>
                """.formatted(safe(nombre), AMBER,
                filaDato("Dirección IP", safe(ip)),
                filaDato("País", safe(pais)),
                filaDato("Ciudad", safe(ciudad)));
        return envoltorio("Acceso desde IP desconocida", AMBER, cuerpo);
    }

    // ---------------------------------------------------------------------

    private String filaDato(String etiqueta, String valor) {
        return "<p style=\"margin:6px 0;\"><span style=\"color:" + PRIMARIO + ";\">"
                + etiqueta + ":</span> " + valor + "</p>";
    }

    private String safe(String valor) {
        return valor != null ? valor : "—";
    }

    private String envoltorio(String titulo, String colorAcento, String contenido) {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <body style="margin:0;padding:0;background:%s;font-family:Segoe UI,Arial,sans-serif;">
                  <div style="max-width:560px;margin:32px auto;background:%s;border-radius:16px;
                              overflow:hidden;border:1px solid rgba(108,99,255,0.25);">
                    <div style="background:linear-gradient(135deg,%s,%s);padding:24px 28px;">
                      <h1 style="margin:0;color:#fff;font-size:22px;letter-spacing:1px;">LYNX</h1>
                      <p style="margin:4px 0 0;color:#fff;opacity:0.85;font-size:13px;">
                        Autenticación Biométrica Dual</p>
                    </div>
                    <div style="padding:28px;color:%s;font-size:15px;line-height:1.6;">
                      <h2 style="color:%s;margin-top:0;">%s</h2>
                      %s
                    </div>
                    <div style="padding:18px 28px;border-top:1px solid rgba(108,99,255,0.2);
                                color:#8a8aa8;font-size:12px;text-align:center;">
                      © 2026 Abel Gomez. Todos los derechos reservados.
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(FONDO, SUPERFICIE, PRIMARIO, colorAcento, TEXTO,
                colorAcento, titulo, contenido);
    }
}
