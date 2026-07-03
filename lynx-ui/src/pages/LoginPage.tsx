/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 * Flujo de login: liveness → rostro → voz → acceso concedido.
 */
import { useEffect, useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { authApi } from '../api/authApi'
import { useAuthStore } from '../store/authStore'
import { useFaceCapture } from '../hooks/useFaceCapture'
import { useVoiceCapture } from '../hooks/useVoiceCapture'
import { useLiveness } from '../hooks/useLiveness'
import { detectarDispositivo } from '../utils/biometricUtils'
import { FaceCapture } from '../components/biometric/FaceCapture'
import { FaceTips } from '../components/biometric/FaceTips'
import { PasoHeader } from '../components/biometric/PasoHeader'
import { VoiceCapture } from '../components/biometric/VoiceCapture'
import { LivenessCheck } from '../components/biometric/LivenessCheck'
import { BiometricStatus } from '../components/biometric/BiometricStatus'
import { Button } from '../components/ui/Button'
import { Card } from '../components/ui/Card'
import { Alert } from '../components/ui/Alert'
import { Spinner } from '../components/ui/Spinner'
import { Copyright } from '../components/ui/Copyright'
import { BiometricBackground } from '../components/ui/BiometricBackground'

type Paso = 'inicio' | 'facial' | 'voz' | 'concedido'

export function LoginPage() {
  const navigate = useNavigate()
  const setSesion = useAuthStore((s) => s.setSesion)
  const face = useFaceCapture()
  const voz = useVoiceCapture()
  const liveness = useLiveness(5)

  const [paso, setPaso] = useState<Paso>('inicio')
  const [cargando, setCargando] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [caraOk, setCaraOk] = useState<boolean | null>(null)
  const [vozOk, setVozOk] = useState<boolean | null>(null)

  const [idUsuario, setIdUsuario] = useState<number | null>(null)
  const [frase, setFrase] = useState('')
  const [confianzaFacial, setConfianzaFacial] = useState<number | undefined>()

  // Al entrar al paso facial solo se prepara el liveness. La cámara la
  // enciende el usuario con el botón (el permiso se pide con un gesto).
  useEffect(() => {
    if (paso === 'facial') liveness.iniciar()
    return () => face.detenerCamara()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [paso])

  const empezar = () => {
    setError(null)
    setCaraOk(null)
    setVozOk(null)
    setPaso('facial')
  }

  // Cancela el inicio de sesión en curso y vuelve a la pantalla inicial
  const cancelarLogin = () => {
    face.detenerCamara()
    if (voz.grabando) voz.detenerGrabacion()
    setError(null)
    setCaraOk(null)
    setVozOk(null)
    setPaso('inicio')
  }

  const [midiendoLiveness, setMidiendoLiveness] = useState(false)

  const verificarRostro = async () => {
    setError(null)
    setCargando(true)
    try {
      // 1) Prueba de vida: registra el movimiento real (~3.5s) mientras el
      //    usuario ejecuta la acción. El backend la verifica (anti-foto).
      setMidiendoLiveness(true)
      const muestras = await face.medirLiveness(3500)
      setMidiendoLiveness(false)
      liveness.confirmar()

      // 2) Captura del embedding facial
      const emb = await face.capturarEmbedding()
      if (!emb) {
        setError('No se detectó tu rostro. Centra tu cara e intenta de nuevo.')
        setCaraOk(false)
        return
      }

      // 3) Envía embedding + acción + serie de muestras al backend
      const res = await authApi.loginFacial(
        emb,
        liveness.accion.codigo,
        muestras,
        undefined,
        detectarDispositivo()
      )
      setCaraOk(true)
      setIdUsuario(res.usuario!.id)
      setFrase(res.frase ?? '')
      setConfianzaFacial(res.confianzaFacial)
      face.detenerCamara()
      setPaso('voz')
    } catch (e: any) {
      setCaraOk(false)
      setError(e?.response?.data?.mensaje ?? 'Rostro no reconocido o prueba de vida fallida.')
    } finally {
      setMidiendoLiveness(false)
      setCargando(false)
    }
  }

  const verificarVoz = async () => {
    if (!voz.audioBlob || idUsuario == null) return
    setError(null)
    setCargando(true)
    try {
      const res = await authApi.loginVoz(
        voz.audioBlob,
        idUsuario,
        frase,
        confianzaFacial,
        detectarDispositivo()
      )
      if (res.accessToken && res.refreshToken && res.usuario) {
        setVozOk(true)
        setSesion(res.accessToken, res.refreshToken, res.usuario)
        setPaso('concedido')
        setTimeout(() => {
          navigate(res.usuario!.rol === 'ADMIN' ? '/admin' : '/dashboard')
        }, 1800)
      } else {
        setVozOk(false)
        setError(res.mensaje ?? 'Voz no verificada.')
      }
    } catch (e: any) {
      setVozOk(false)
      setError(e?.response?.data?.mensaje ?? 'Voz no verificada. Acceso denegado.')
    } finally {
      setCargando(false)
    }
  }

  return (
    <div className="min-h-screen flex flex-col items-center justify-center p-6 relative">
      <BiometricBackground />
      <div className="relative z-10 w-full flex flex-col items-center">
      <motion.h1
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="text-5xl font-bold tracking-widest text-lynx-primary mb-2 lynx-glow"
      >
        LYNX
      </motion.h1>
      <p className="text-lynx-text/60 mb-8 text-sm">Autenticación Biométrica Dual · Cara + Voz</p>

      <motion.div
        initial={{ opacity: 0, y: 24, scale: 0.98 }}
        animate={{ opacity: 1, y: 0, scale: 1 }}
        transition={{ duration: 0.5, ease: 'easeOut' }}
        className="w-full max-w-xl"
      >
      <Card className="w-full flex flex-col items-center gap-6">
        {error && <Alert tipo="error" mensaje={error} />}

        {paso === 'inicio' && (
          <div className="text-center space-y-6 py-8">
            <p className="text-lynx-text/80">Sin contraseñas. Tu cara y tu voz son tu identidad.</p>
            <Button onClick={empezar}>Iniciar sesión con biometría</Button>
            <p className="text-sm text-lynx-text/50">
              ¿Aún no te registras?{' '}
              <Link to="/register" className="text-lynx-primary">
                Crear identidad biométrica
              </Link>
            </p>
          </div>
        )}

        {paso === 'facial' && (
          <>
            <PasoHeader
              numero={1}
              total={2}
              titulo="Factor 1 · Reconocimiento facial"
              descripcion="Realiza la acción de seguridad indicada y centra tu rostro en el óvalo. Cuando el recuadro esté verde, verifica."
            />
            {face.error && (
              <>
                <Alert tipo="error" mensaje={face.error} />
                <Button variant="ghost" onClick={() => window.location.reload()}>
                  Recargar página
                </Button>
              </>
            )}
            {face.cargandoModelos && (
              <Alert tipo="info" mensaje="Cargando modelos de reconocimiento facial..." />
            )}
            <LivenessCheck
              accion={liveness.accion}
              restante={liveness.restante}
              midiendo={midiendoLiveness}
            />
            <FaceCapture
              videoRef={face.videoRef}
              estado={caraOk === false ? 'rojo' : face.estado}
              mensaje={face.mensaje}
              confianza={face.confianza}
            />
            {!face.camaraActiva && (
              <Button variant="secondary" onClick={face.iniciarCamara}>
                📷 Encender cámara
              </Button>
            )}
            <FaceTips />
            <Button
              onClick={verificarRostro}
              disabled={cargando || !face.modelsLoaded || !face.listoParaCapturar || !face.camaraActiva}
            >
              {cargando ? <Spinner size={18} /> : 'Verificar rostro'}
            </Button>
            {!face.listoParaCapturar && face.modelsLoaded && (
              <p className="text-xs text-lynx-warning text-center">
                El botón se activa cuando el recuadro esté en verde.
              </p>
            )}
            <button onClick={cancelarLogin} className="text-xs text-lynx-text/50 hover:text-lynx-error">
              Cancelar
            </button>
          </>
        )}

        {paso === 'voz' && (
          <>
            <PasoHeader
              numero={2}
              total={2}
              titulo="Factor 2 · Verificación de voz"
              descripcion="Pulsa Grabar y lee la frase mostrada, clara y completa. Puedes escucharte y regrabar antes de verificar. La frase cambia cada vez (anti-replay)."
            />
            {voz.error && <Alert tipo="error" mensaje={voz.error} />}
            <VoiceCapture
              frase={frase}
              grabando={voz.grabando}
              duracion={voz.duracion}
              nivelAudio={voz.nivelAudio}
              audioBlob={voz.audioBlob}
            />
            {!voz.micActivo && (
              <Button variant="secondary" onClick={voz.encenderMicrofono}>
                🎙️ Encender micrófono
              </Button>
            )}
            <div className="flex gap-3">
              {!voz.grabando ? (
                <Button variant="secondary" onClick={voz.iniciarGrabacion}>
                  {voz.audioBlob ? 'Grabar de nuevo' : 'Grabar voz'}
                </Button>
              ) : (
                <Button variant="danger" onClick={voz.detenerGrabacion}>
                  Detener
                </Button>
              )}
              <Button onClick={verificarVoz} disabled={!voz.audioBlob || cargando}>
                {cargando ? <Spinner size={18} /> : 'Verificar voz'}
              </Button>
            </div>
            <button onClick={cancelarLogin} className="text-xs text-lynx-text/50 hover:text-lynx-error">
              Cancelar
            </button>
          </>
        )}

        {paso === 'concedido' && (
          <motion.div
            initial={{ opacity: 0, scale: 0.8 }}
            animate={{ opacity: 1, scale: 1 }}
            className="text-center py-10"
          >
            <p className="text-6xl mb-4">🔓</p>
            <p className="text-2xl font-semibold text-lynx-secondary">Acceso concedido</p>
            <p className="text-lynx-text/60 mt-2">Entrando al panel...</p>
          </motion.div>
        )}

        {paso !== 'inicio' && paso !== 'concedido' && (
          <BiometricStatus caraOk={caraOk} vozOk={vozOk} />
        )}
      </Card>
      </motion.div>

      <Copyright />
      </div>
    </div>
  )
}
