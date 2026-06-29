/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
package com.lynx.auth.service.impl;

import com.lynx.auth.client.FaceServiceClient;
import com.lynx.auth.client.VoiceServiceClient;
import com.lynx.auth.client.dto.FaceRegistroDto;
import com.lynx.auth.client.dto.FaceVerificacionDto;
import com.lynx.auth.client.dto.FaceVerificarDto;
import com.lynx.auth.client.dto.VoiceRegistroDto;
import com.lynx.auth.client.dto.VoiceVerificacionDto;
import com.lynx.auth.client.dto.VoiceprintDto;
import com.lynx.auth.config.KafkaConfig;
import com.lynx.auth.dto.request.LoginFaceRequest;
import com.lynx.auth.dto.request.LoginVoiceRequest;
import com.lynx.auth.dto.request.RefreshRequest;
import com.lynx.auth.dto.request.RegistroFaceRequest;
import com.lynx.auth.dto.request.RegistroRequest;
import com.lynx.auth.dto.response.AuthResponse;
import com.lynx.auth.dto.response.UsuarioResponse;
import com.lynx.auth.entity.RefreshToken;
import com.lynx.auth.entity.Usuario;
import com.lynx.auth.enums.EstadoRegistro;
import com.lynx.auth.enums.FactorFallido;
import com.lynx.auth.enums.Rol;
import com.lynx.auth.exception.AuthenticationException;
import com.lynx.auth.exception.CuentaBloqueadaException;
import com.lynx.auth.exception.RegistroException;
import com.lynx.auth.exception.RostroNoReconocidoException;
import com.lynx.auth.exception.TokenException;
import com.lynx.auth.exception.UsuarioNotFoundException;
import com.lynx.auth.exception.VozNoVerificadaException;
import com.lynx.auth.mapper.UsuarioMapper;
import com.lynx.auth.repository.RefreshTokenRepository;
import com.lynx.auth.repository.UsuarioRepository;
import com.lynx.auth.service.AuthService;
import com.lynx.auth.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/** Implementación de la lógica de autenticación biométrica dual de Lynx. */
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private FaceServiceClient faceServiceClient;

    @Autowired
    private VoiceServiceClient voiceServiceClient;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    @Value("${biometria.max-intentos-fallidos}")
    private int maxIntentosFallidos;

    @Value("${biometria.minutos-bloqueo}")
    private int minutosBloqueo;

    @Value("${biometria.frases-ttl-segundos}")
    private long frasesTtlSegundos;

    private static final Random RANDOM = new Random();
    private static final String PREFIJO_FRASE = "frase:";

    /** Pool de 50 frases en español latinoamericano natural (anti-replay). */
    private static final List<String> POOL_FRASES = List.of(
            "Las montañas de Azuay son verdes en mayo",
            "El mercado de Cuenca abre muy temprano",
            "Los colibríes vuelan muy rápido entre flores",
            "El río Tomebamba cruza Cuenca de oeste a este",
            "En enero llueve mucho en la sierra ecuatoriana",
            "El café de la mañana huele delicioso",
            "Los niños juegan en el parque al atardecer",
            "La música andina suena en cada esquina",
            "El sol brilla intenso sobre los Andes",
            "Las nubes cubren los volcanes en invierno",
            "El tren antiguo recorre la nariz del diablo",
            "Los artesanos tejen sombreros de paja toquilla",
            "El lago refleja el cielo azul de la tarde",
            "Las flores del jardín abren con el rocío",
            "El viento mueve las hojas de los árboles altos",
            "La cascada cae con fuerza sobre las rocas",
            "Los pescadores salen al mar antes del amanecer",
            "El pan caliente sale del horno de leña",
            "Las estrellas iluminan el cielo despejado de noche",
            "El camino de piedra lleva hasta la laguna",
            "Los caballos corren libres por el valle verde",
            "La lluvia suave cae sobre los techos rojos",
            "El mercado vende frutas frescas cada domingo",
            "Las campanas de la iglesia suenan al mediodía",
            "El puente cruza el río de aguas cristalinas",
            "Los turistas suben al mirador de la ciudad",
            "El bosque húmedo guarda muchos secretos antiguos",
            "La abuela prepara colada morada en noviembre",
            "Los danzantes giran al ritmo del tambor",
            "El cóndor planea sobre las cumbres nevadas",
            "Las olas rompen suaves en la playa dorada",
            "El faro guía a los barcos en la noche oscura",
            "Los girasoles miran siempre hacia el sol naciente",
            "La nieve cubre el páramo durante la madrugada",
            "El colibrí bebe néctar de la flor roja",
            "Los granos de cacao secan bajo el sol caliente",
            "El sendero serpentea entre los pinos altos",
            "La feria llena las calles de colores vivos",
            "El arroyo murmura entre las piedras del bosque",
            "Los pájaros cantan al despuntar la mañana fresca",
            "El telar produce mantas de lana suave",
            "La hoguera calienta la noche fría del campo",
            "Los pétalos caen lentamente sobre el agua quieta",
            "El reloj de la plaza marca las doce en punto",
            "Las luciérnagas brillan en el jardín de noche",
            "El maíz crece dorado en los campos amplios",
            "La canoa se desliza sobre el río tranquilo",
            "Los volcanes duermen bajo un manto de nubes",
            "El aroma del eucalipto llena el aire limpio",
            "La luna llena ilumina el sendero del valle"
    );

    @Override
    @Transactional
    public UsuarioResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RegistroException("EMAIL_DUPLICADO", "Ya existe un usuario con ese email");
        }
        Rol rol = request.getRol() != null ? Rol.valueOf(request.getRol().toUpperCase()) : Rol.EMPLEADO;
        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .rol(rol)
                .departamento(request.getDepartamento())
                .estadoRegistro(EstadoRegistro.PENDIENTE_BIOMETRIA)
                .esActivo(true)
                .intentosFallidos(0)
                .build();
        usuario = usuarioRepository.save(usuario);
        log.info("Usuario registrado: {} ({})", usuario.getNombre(), usuario.getEmail());
        return usuarioMapper.toResponse(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponse registrarRostro(RegistroFaceRequest request) {
        Usuario usuario = obtenerUsuario(request.getIdUsuario());
        FaceRegistroDto dto = FaceRegistroDto.builder()
                .idUsuario(usuario.getId())
                .embedding(request.getEmbedding())
                .fotoReferencia(request.getFotoReferencia())
                .build();
        faceServiceClient.registrarRostro(dto);
        usuario.setEstadoRegistro(EstadoRegistro.CARA_REGISTRADA);
        usuarioRepository.save(usuario);
        log.info("Rostro registrado para usuario {}", usuario.getId());
        return usuarioMapper.toResponse(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponse registrarVoz(MultipartFile audio, String fraseEsperada, Long idUsuario) {
        Usuario usuario = obtenerUsuario(idUsuario);
        VoiceRegistroDto resultado = voiceServiceClient.registrarVoz(
                audio, fraseEsperada, String.valueOf(idUsuario));
        if (resultado.getExitoso() == null || !resultado.getExitoso()) {
            throw new VozNoVerificadaException("La frase leída no coincide. Intenta de nuevo.");
        }
        faceServiceClient.registrarVoiceprint(VoiceprintDto.builder()
                .idUsuario(idUsuario)
                .voiceprint(resultado.getVoiceprint())
                .build());
        usuario.setEstadoRegistro(EstadoRegistro.COMPLETO);
        usuarioRepository.save(usuario);
        log.info("Voz registrada. Registro biométrico COMPLETO para usuario {}", idUsuario);
        return usuarioMapper.toResponse(usuario);
    }

    @Override
    @Transactional
    public AuthResponse loginFacial(LoginFaceRequest request) {
        FaceVerificarDto dto = FaceVerificarDto.builder()
                .embedding(request.getEmbedding())
                .livenessSuperado(true)
                .ipAddress(request.getIpAddress())
                .build();
        FaceVerificacionDto verif = faceServiceClient.verificarRostro(dto);

        if (verif.getReconocido() == null || !verif.getReconocido()) {
            publicar(KafkaConfig.TOPIC_ACCESO_FALLIDO, payloadFallido(null, null,
                    request.getIpAddress(), FactorFallido.FACIAL, null));
            throw new RostroNoReconocidoException("Rostro no reconocido. Acceso denegado.");
        }

        Usuario usuario = obtenerUsuario(verif.getIdUsuario());
        verificarBloqueo(usuario);

        String frase = generarFrase(usuario.getId());
        log.info("Factor facial superado para usuario {} (confianza {})",
                usuario.getId(), verif.getConfianza());

        return AuthResponse.builder()
                .usuario(usuarioMapper.toResponse(usuario))
                .confianzaFacial(verif.getConfianza())
                .requiereSegundoFactor(true)
                .frase(frase)
                .mensaje("Factor facial verificado. Lee la frase para completar el acceso.")
                .build();
    }

    @Override
    @Transactional
    public AuthResponse loginVoz(MultipartFile audio, LoginVoiceRequest request) {
        Usuario usuario = obtenerUsuario(request.getIdUsuario());
        verificarBloqueo(usuario);

        // La frase válida se obtiene de Redis (anti-replay con TTL)
        String fraseValida = redisTemplate.opsForValue().get(PREFIJO_FRASE + usuario.getId());
        if (fraseValida == null) {
            throw new VozNoVerificadaException("La frase ha expirado. Solicita una nueva e intenta otra vez.");
        }

        VoiceprintDto voiceprint = faceServiceClient.obtenerVoiceprint(usuario.getId());
        String voiceprintCsv = voiceprint.getVoiceprint().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        VoiceVerificacionDto verif = voiceServiceClient.verificarVoz(audio, fraseValida, voiceprintCsv);

        if (verif.getExitoso() == null || !verif.getExitoso()) {
            FactorFallido factor = (verif.getTextoCorrecto() != null && !verif.getTextoCorrecto())
                    ? FactorFallido.VOZ : FactorFallido.VOZ;
            registrarFallo(usuario, factor, request.getIpAddress());
            throw new VozNoVerificadaException("Voz no verificada. Acceso denegado.");
        }

        // Éxito: limpiar contador y frase, emitir tokens
        usuario.setIntentosFallidos(0);
        usuario.setBloqueadoHasta(null);
        usuarioRepository.save(usuario);
        redisTemplate.delete(PREFIJO_FRASE + usuario.getId());

        String accessToken = jwtService.generarAccessToken(usuario,
                request.getConfianzaFacial(), verif.getConfianza());
        String refreshToken = crearRefreshToken(usuario.getId());

        publicar(KafkaConfig.TOPIC_ACCESO_EXITOSO, payloadExitoso(usuario,
                request.getIpAddress(), request.getDispositivo(),
                request.getConfianzaFacial(), verif.getConfianza()));

        log.info("Acceso concedido a usuario {} ({})", usuario.getId(), usuario.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .usuario(usuarioMapper.toResponse(usuario))
                .confianzaFacial(request.getConfianzaFacial())
                .confianzaVoz(verif.getConfianza())
                .requiereSegundoFactor(false)
                .mensaje("Acceso concedido. Bienvenido, " + usuario.getNombre() + ".")
                .build();
    }

    @Override
    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken actual = refreshTokenRepository
                .findByTokenAndEsValidoTrue(request.getRefreshToken())
                .orElseThrow(() -> new TokenException("REFRESH_INVALIDO", "Refresh token inválido o ya usado"));

        if (actual.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            actual.setEsValido(false);
            refreshTokenRepository.save(actual);
            throw new TokenException("REFRESH_EXPIRADO", "El refresh token ha expirado. Inicia sesión de nuevo.");
        }

        // Rotación: invalidar el anterior y emitir uno nuevo
        actual.setEsValido(false);
        refreshTokenRepository.save(actual);

        Usuario usuario = obtenerUsuario(actual.getIdUsuario());
        String nuevoAccess = jwtService.generarAccessToken(usuario, null, null);
        String nuevoRefresh = crearRefreshToken(usuario.getId());

        return AuthResponse.builder()
                .accessToken(nuevoAccess)
                .refreshToken(nuevoRefresh)
                .usuario(usuarioMapper.toResponse(usuario))
                .requiereSegundoFactor(false)
                .mensaje("Tokens renovados.")
                .build();
    }

    @Override
    @Transactional
    public void logout(RefreshRequest request) {
        refreshTokenRepository.findByTokenAndEsValidoTrue(request.getRefreshToken())
                .ifPresent(rt -> {
                    rt.setEsValido(false);
                    refreshTokenRepository.save(rt);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerActual(Long idUsuario) {
        return usuarioMapper.toResponse(obtenerUsuario(idUsuario));
    }

    @Override
    public String generarFrase(Long idUsuario) {
        String frase = POOL_FRASES.get(RANDOM.nextInt(POOL_FRASES.size()));
        redisTemplate.opsForValue().set(PREFIJO_FRASE + idUsuario, frase,
                frasesTtlSegundos, TimeUnit.SECONDS);
        return frase;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UsuarioResponse bloquearUsuario(Long idUsuario) {
        Usuario usuario = obtenerUsuario(idUsuario);
        usuario.setEsActivo(false);
        usuario.setBloqueadoHasta(LocalDateTime.now().plusYears(100));
        usuarioRepository.save(usuario);
        log.info("Usuario {} bloqueado por administrador", idUsuario);
        return usuarioMapper.toResponse(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponse desbloquearUsuario(Long idUsuario) {
        Usuario usuario = obtenerUsuario(idUsuario);
        usuario.setEsActivo(true);
        usuario.setBloqueadoHasta(null);
        usuario.setIntentosFallidos(0);
        usuarioRepository.save(usuario);
        log.info("Usuario {} desbloqueado por administrador", idUsuario);
        return usuarioMapper.toResponse(usuario);
    }

    // ---------------------------------------------------------------------
    //  Métodos auxiliares
    // ---------------------------------------------------------------------

    private Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado: " + id));
    }

    private void verificarBloqueo(Usuario usuario) {
        if (usuario.getBloqueadoHasta() != null
                && usuario.getBloqueadoHasta().isAfter(LocalDateTime.now())) {
            throw new CuentaBloqueadaException("Cuenta bloqueada hasta " + usuario.getBloqueadoHasta());
        }
    }

    private void registrarFallo(Usuario usuario, FactorFallido factor, String ip) {
        int intentos = usuario.getIntentosFallidos() + 1;
        usuario.setIntentosFallidos(intentos);

        int restantes = maxIntentosFallidos - intentos;
        publicar(KafkaConfig.TOPIC_ACCESO_FALLIDO,
                payloadFallido(usuario.getId(), usuario.getEmail(), ip, factor, Math.max(restantes, 0)));

        if (intentos >= maxIntentosFallidos) {
            LocalDateTime hasta = LocalDateTime.now().plusMinutes(minutosBloqueo);
            usuario.setBloqueadoHasta(hasta);
            usuario.setIntentosFallidos(0);
            publicar(KafkaConfig.TOPIC_CUENTA_BLOQUEADA, payloadBloqueo(usuario, minutosBloqueo));
            log.warn("Usuario {} bloqueado por {} minutos", usuario.getId(), minutosBloqueo);
        }
        usuarioRepository.save(usuario);
    }

    private String crearRefreshToken(Long idUsuario) {
        String token = jwtService.generarRefreshToken();
        RefreshToken rt = RefreshToken.builder()
                .token(token)
                .idUsuario(idUsuario)
                .esValido(true)
                .fechaExpiracion(LocalDateTime.now().plusSeconds(refreshExpiration / 1000))
                .build();
        refreshTokenRepository.save(rt);
        return token;
    }

    private void publicar(String topic, Map<String, Object> payload) {
        try {
            kafkaTemplate.send(topic, payload);
        } catch (Exception ex) {
            log.error("No se pudo publicar en Kafka topic {}: {}", topic, ex.getMessage());
        }
    }

    private Map<String, Object> payloadExitoso(Usuario u, String ip, String dispositivo,
                                               Double cFacial, Double cVoz) {
        Map<String, Object> p = new HashMap<>();
        p.put("idUsuario", u.getId());
        p.put("email", u.getEmail());
        p.put("nombreUsuario", u.getNombre());
        p.put("ipAddress", ip);
        p.put("dispositivo", dispositivo);
        p.put("confianzaFacial", cFacial);
        p.put("confianzaVoz", cVoz);
        p.put("timestamp", LocalDateTime.now().toString());
        return p;
    }

    private Map<String, Object> payloadFallido(Long idUsuario, String email, String ip,
                                               FactorFallido factor, Integer restantes) {
        Map<String, Object> p = new HashMap<>();
        p.put("idUsuario", idUsuario);
        p.put("email", email);
        p.put("ipAddress", ip);
        p.put("factorFallido", factor.name());
        p.put("intentosRestantes", restantes);
        p.put("timestamp", LocalDateTime.now().toString());
        return p;
    }

    private Map<String, Object> payloadBloqueo(Usuario u, int minutos) {
        Map<String, Object> p = new HashMap<>();
        p.put("idUsuario", u.getId());
        p.put("email", u.getEmail());
        p.put("nombreUsuario", u.getNombre());
        p.put("minutosBloqueo", minutos);
        p.put("timestamp", LocalDateTime.now().toString());
        return p;
    }
}
