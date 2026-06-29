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
import { VoiceCapture } from '../components/biometric/VoiceCapture'
import { LivenessCheck } from '../components/biometric/LivenessCheck'
import { BiometricStatus } from '../components/biometric/BiometricStatus'
import { Button } from '../components/ui/Button'
import { Card } from '../components/ui/Card'
import { Alert } from '../components/ui/Alert'
import { Spinner } from '../components/ui/Spinner'
import { Copyright } from '../components/ui/Copyright'

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

  // Inicia la cámara al entrar al paso facial
  useEffect(() => {
    if (paso === 'facial') {
      face.iniciarCamara()
      liveness.iniciar()
    }
    return () => face.detenerCamara()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [paso])

  const empezar = () => {
    setError(null)
    setCaraOk(null)
    setVozOk(null)
    setPaso('facial')
  }

  const verificarRostro = async () => {
    setError(null)
    setCargando(true)
    try {
      liveness.confirmar()
      const emb = await face.capturarEmbedding()
      if (!emb) {
        setError('No se detectó tu rostro. Centra tu cara e intenta de nuevo.')
        setCaraOk(false)
        return
      }
      const res = await authApi.loginFacial(emb, undefined, detectarDispositivo())
      setCaraOk(true)
      setIdUsuario(res.usuario!.id)
      setFrase(res.frase ?? '')
      setConfianzaFacial(res.confianzaFacial)
      face.detenerCamara()
      setPaso('voz')
    } catch (e: any) {
      setCaraOk(false)
      setError(e?.response?.data?.mensaje ?? 'Rostro no reconocido. Acceso denegado.')
    } finally {
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
    <div className="min-h-screen flex flex-col items-center justify-center p-6">
      <motion.h1
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="text-5xl font-bold tracking-widest text-lynx-primary mb-2"
      >
        LYNX
      </motion.h1>
      <p className="text-lynx-text/60 mb-8 text-sm">Autenticación Biométrica Dual · Cara + Voz</p>

      <Card className="w-full max-w-xl flex flex-col items-center gap-6">
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
            {face.error && <Alert tipo="error" mensaje={face.error} />}
            {face.cargandoModelos && (
              <Alert tipo="info" mensaje="Cargando modelos de reconocimiento facial..." />
            )}
            <LivenessCheck accion={liveness.accion} restante={liveness.restante} />
            <FaceCapture
              videoRef={face.videoRef}
              rostroDetectado={face.rostroDetectado}
              confianza={face.confianza}
              estado={caraOk === false ? 'error' : caraOk ? 'ok' : face.rostroDetectado ? 'ok' : 'idle'}
              instruccion={face.mensaje}
            />
            <Button
              onClick={verificarRostro}
              disabled={cargando || !face.modelsLoaded || !face.rostroDetectado}
            >
              {cargando ? <Spinner size={18} /> : 'Verificar rostro'}
            </Button>
            {!face.rostroDetectado && face.modelsLoaded && (
              <p className="text-xs text-lynx-warning text-center">
                El botón se activa cuando se detecte tu rostro.
              </p>
            )}
          </>
        )}

        {paso === 'voz' && (
          <>
            <VoiceCapture
              frase={frase}
              grabando={voz.grabando}
              duracion={voz.duracion}
              nivelAudio={voz.nivelAudio}
              audioBlob={voz.audioBlob}
            />
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

      <Copyright />
    </div>
  )
}
