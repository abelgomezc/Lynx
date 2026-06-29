/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 * Registro en 3 pasos: datos → rostro → voz.
 */
import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { authApi } from '../api/authApi'
import { useFaceCapture } from '../hooks/useFaceCapture'
import { useVoiceCapture } from '../hooks/useVoiceCapture'
import { capturarFoto } from '../utils/biometricUtils'
import { FaceCapture } from '../components/biometric/FaceCapture'
import { VoiceCapture } from '../components/biometric/VoiceCapture'
import { Button } from '../components/ui/Button'
import { Card } from '../components/ui/Card'
import { Alert } from '../components/ui/Alert'
import { Spinner } from '../components/ui/Spinner'
import { Copyright } from '../components/ui/Copyright'
import { SearchableSelect } from '../components/ui/SearchableSelect'

type Paso = 'datos' | 'rostro' | 'voz' | 'completo'

// Departamentos disponibles para el buscador del registro
const DEPARTAMENTOS = [
  'Sistemas / TI',
  'Recursos Humanos',
  'Contabilidad',
  'Finanzas',
  'Ventas',
  'Marketing',
  'Operaciones',
  'Logística',
  'Atención al Cliente',
  'Producción',
  'Compras',
  'Legal',
  'Administración',
  'Gerencia',
  'Calidad',
  'Mantenimiento',
  'Seguridad',
  'Investigación y Desarrollo',
]

export function RegisterPage() {
  const navigate = useNavigate()
  const face = useFaceCapture()
  const voz = useVoiceCapture()

  const [paso, setPaso] = useState<Paso>('datos')
  const [cargando, setCargando] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const [nombre, setNombre] = useState('')
  const [email, setEmail] = useState('')
  const [departamento, setDepartamento] = useState('')
  // El rol siempre se crea como EMPLEADO; el administrador lo ajusta luego.

  const [idUsuario, setIdUsuario] = useState<number | null>(null)
  const [frase, setFrase] = useState('')
  // Previsualización de la foto antes de confirmar (permite repetir)
  const [fotoPreview, setFotoPreview] = useState<string | null>(null)
  const [embTmp, setEmbTmp] = useState<number[] | null>(null)

  useEffect(() => {
    if (paso === 'rostro') face.iniciarCamara()
    return () => face.detenerCamara()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [paso])

  const crearUsuario = async () => {
    setError(null)
    setCargando(true)
    try {
      const u = await authApi.registrar({ nombre, email, rol: 'EMPLEADO', departamento })
      setIdUsuario(u.id)
      setPaso('rostro')
    } catch (e: any) {
      setError(e?.response?.data?.mensaje ?? 'No se pudo crear el usuario.')
    } finally {
      setCargando(false)
    }
  }

  // Toma la foto + embedding y los deja en previsualización (no envía aún)
  const capturarRostro = async () => {
    setError(null)
    setCargando(true)
    try {
      const emb = await face.capturarEmbedding()
      if (!emb) {
        setError('No se detectó tu rostro. Céntrate e inténtalo de nuevo.')
        return
      }
      const foto = face.videoRef.current ? capturarFoto(face.videoRef.current) : null
      setEmbTmp(emb)
      setFotoPreview(foto)
      face.detenerCamara()
    } catch {
      setError('No se pudo capturar el rostro. Inténtalo de nuevo.')
    } finally {
      setCargando(false)
    }
  }

  // Descarta la foto y reactiva la cámara para repetir
  const repetirRostro = () => {
    setFotoPreview(null)
    setEmbTmp(null)
    face.iniciarCamara()
  }

  // Confirma la foto previsualizada: la registra y pasa a la voz
  const confirmarRostro = async () => {
    if (idUsuario == null || !embTmp) return
    setError(null)
    setCargando(true)
    try {
      await authApi.registrarRostro(idUsuario, embTmp, fotoPreview ?? undefined)
      const { frase } = await authApi.generarFrase(idUsuario)
      setFrase(frase)
      setPaso('voz')
    } catch (e: any) {
      setError(e?.response?.data?.mensaje ?? 'No se pudo registrar el rostro.')
    } finally {
      setCargando(false)
    }
  }

  const registrarVoz = async () => {
    if (idUsuario == null || !voz.audioBlob) return
    setError(null)
    setCargando(true)
    try {
      await authApi.registrarVoz(voz.audioBlob, frase, idUsuario)
      setPaso('completo')
    } catch (e: any) {
      setError(e?.response?.data?.mensaje ?? 'La frase no coincide. Intenta de nuevo.')
    } finally {
      setCargando(false)
    }
  }

  return (
    <div className="min-h-screen flex flex-col items-center justify-center p-6">
      <h1 className="text-4xl font-bold tracking-widest text-lynx-primary mb-1">LYNX</h1>
      <p className="text-lynx-text/60 mb-8 text-sm">Crea tu identidad biométrica</p>

      <Card className="w-full max-w-xl flex flex-col items-center gap-6">
        <div className="flex gap-2 text-xs text-lynx-text/50">
          <span className={paso === 'datos' ? 'text-lynx-primary' : ''}>1. Datos</span>·
          <span className={paso === 'rostro' ? 'text-lynx-primary' : ''}>2. Rostro</span>·
          <span className={paso === 'voz' ? 'text-lynx-primary' : ''}>3. Voz</span>
        </div>

        {error && <Alert tipo="error" mensaje={error} />}

        {paso === 'datos' && (
          <div className="w-full max-w-sm space-y-3">
            <input
              className="w-full bg-lynx-surface border border-lynx-primary/30 rounded-xl px-4 py-2.5 outline-none focus:border-lynx-primary"
              placeholder="Nombre completo"
              value={nombre}
              onChange={(e) => setNombre(e.target.value)}
            />
            <input
              className="w-full bg-lynx-surface border border-lynx-primary/30 rounded-xl px-4 py-2.5 outline-none focus:border-lynx-primary"
              placeholder="Email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
            <SearchableSelect
              options={DEPARTAMENTOS}
              value={departamento}
              onChange={setDepartamento}
              placeholder="Departamento (escribe para buscar)"
            />
            <p className="text-xs text-lynx-text/50 px-1">
              Te registramos como <span className="text-lynx-primary">Empleado</span>.
              El administrador puede cambiar tu rol después.
            </p>
            <Button className="w-full" onClick={crearUsuario} disabled={cargando || !nombre || !email}>
              {cargando ? <Spinner size={18} /> : 'Siguiente →'}
            </Button>
          </div>
        )}

        {paso === 'rostro' && (
          <>
            {face.error && <Alert tipo="error" mensaje={face.error} />}
            {face.cargandoModelos && (
              <Alert tipo="info" mensaje="Cargando modelos de reconocimiento facial..." />
            )}

            {!fotoPreview ? (
              <>
                <FaceCapture
                  videoRef={face.videoRef}
                  rostroDetectado={face.rostroDetectado}
                  confianza={face.confianza}
                  estado={face.rostroDetectado ? 'ok' : 'idle'}
                  instruccion={face.mensaje}
                />
                <Button
                  onClick={capturarRostro}
                  disabled={cargando || !face.modelsLoaded || !face.rostroDetectado}
                >
                  {cargando ? <Spinner size={18} /> : 'Capturar foto'}
                </Button>
                {!face.rostroDetectado && face.modelsLoaded && (
                  <p className="text-xs text-lynx-warning text-center">
                    El botón se activa cuando se detecte tu rostro.
                  </p>
                )}
              </>
            ) : (
              <>
                <p className="text-lynx-secondary text-sm">
                  ¿Te gusta esta foto? Puedes continuar o repetirla.
                </p>
                <img
                  src={fotoPreview}
                  alt="Tu foto capturada"
                  className="rounded-2xl border-4 border-lynx-secondary"
                  width={420}
                />
                <div className="flex gap-3">
                  <Button variant="ghost" onClick={repetirRostro} disabled={cargando}>
                    Repetir foto
                  </Button>
                  <Button onClick={confirmarRostro} disabled={cargando}>
                    {cargando ? <Spinner size={18} /> : 'Usar esta foto y continuar'}
                  </Button>
                </div>
              </>
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
              <Button onClick={registrarVoz} disabled={!voz.audioBlob || cargando}>
                {cargando ? <Spinner size={18} /> : 'Completar registro'}
              </Button>
            </div>
          </>
        )}

        {paso === 'completo' && (
          <motion.div
            initial={{ opacity: 0, scale: 0.8 }}
            animate={{ opacity: 1, scale: 1 }}
            className="text-center py-10"
          >
            <p className="text-6xl mb-4">✅</p>
            <p className="text-2xl font-semibold text-lynx-secondary">¡Identidad registrada!</p>
            <p className="text-lynx-text/60 mt-2">Ya puedes entrar sin contraseña.</p>
            <Button className="mt-6" onClick={() => navigate('/login')}>
              Ir a iniciar sesión
            </Button>
          </motion.div>
        )}

        {paso === 'datos' && (
          <p className="text-sm text-lynx-text/50">
            ¿Ya tienes identidad?{' '}
            <Link to="/login" className="text-lynx-primary">
              Inicia sesión
            </Link>
          </p>
        )}
      </Card>

      <Copyright />
    </div>
  )
}
