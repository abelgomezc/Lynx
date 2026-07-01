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
import { FaceTips } from '../components/biometric/FaceTips'
import { PasoHeader } from '../components/biometric/PasoHeader'
import { VoiceCapture } from '../components/biometric/VoiceCapture'
import { Button } from '../components/ui/Button'
import { Card } from '../components/ui/Card'
import { Alert } from '../components/ui/Alert'
import { Spinner } from '../components/ui/Spinner'
import { Copyright } from '../components/ui/Copyright'
import { SearchableSelect } from '../components/ui/SearchableSelect'
import { BiometricBackground } from '../components/ui/BiometricBackground'

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

  const [frase, setFrase] = useState('')
  // Errores de validación por campo del paso "Datos"
  const [errores, setErrores] = useState<{ nombre?: string; email?: string; departamento?: string }>({})
  // Previsualización de la foto antes de confirmar (permite repetir)
  const [fotoPreview, setFotoPreview] = useState<string | null>(null)
  const [embTmp, setEmbTmp] = useState<number[] | null>(null)

  // Al salir del paso rostro se libera la cámara. NO se arranca sola:
  // el usuario la enciende con el botón (así el permiso se pide con un gesto).
  useEffect(() => {
    return () => face.detenerCamara()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [paso])

  // Valida los campos del paso "Datos" y devuelve true si todo es correcto
  const validarDatos = () => {
    const e: { nombre?: string; email?: string; departamento?: string } = {}
    const n = nombre.trim()
    const em = email.trim()

    if (!n) e.nombre = 'El nombre es obligatorio.'
    else if (n.length < 3) e.nombre = 'Debe tener al menos 3 caracteres.'
    else if (!/^[a-zA-ZÁÉÍÓÚáéíóúÜüÑñ' ]+$/.test(n)) e.nombre = 'Solo se permiten letras y espacios.'

    if (!em) e.email = 'El email es obligatorio.'
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(em)) e.email = 'El email no tiene un formato válido.'

    if (!departamento.trim()) e.departamento = 'Selecciona o escribe un departamento.'

    setErrores(e)
    return Object.keys(e).length === 0
  }

  // Paso 1 → 2: valida y comprueba el email, pero NO guarda nada todavía.
  const irARostro = async () => {
    setError(null)
    if (!validarDatos()) return
    setCargando(true)
    try {
      const libre = await authApi.emailDisponible(email.trim().toLowerCase())
      if (!libre) {
        setErrores((p) => ({ ...p, email: 'Ya existe un usuario con ese email.' }))
        return
      }
      setPaso('rostro')
    } catch {
      // Si el chequeo falla, continúa: el email se validará al guardar al final.
      setPaso('rostro')
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

  // Acepta la foto (queda en memoria) y prepara la frase para el paso de voz.
  const confirmarRostro = async () => {
    if (!embTmp) return
    setError(null)
    setCargando(true)
    try {
      const f = await authApi.fraseRegistro()
      setFrase(f)
      setPaso('voz')
    } catch {
      setError('No se pudo preparar el paso de voz. Inténtalo de nuevo.')
    } finally {
      setCargando(false)
    }
  }

  // Paso final: envía TODO junto. Solo aquí se guarda; si falla, no queda nada.
  const completarRegistro = async () => {
    if (!voz.audioBlob || !embTmp) return
    setError(null)
    setCargando(true)
    try {
      await authApi.registrarCompleto(
        {
          nombre: nombre.trim(),
          email: email.trim().toLowerCase(),
          rol: 'EMPLEADO',
          departamento: departamento.trim(),
        },
        embTmp,
        voz.audioBlob,
        frase,
        fotoPreview ?? undefined
      )
      setPaso('completo')
    } catch (e: any) {
      setError(e?.response?.data?.mensaje ?? 'No se pudo completar el registro. No se guardó nada.')
    } finally {
      setCargando(false)
    }
  }

  // Cancela el registro en curso: libera cámara/micrófono y vuelve al login
  const cancelarRegistro = () => {
    face.detenerCamara()
    if (voz.grabando) voz.detenerGrabacion()
    navigate('/login')
  }

  return (
    <div className="min-h-screen flex flex-col items-center justify-center p-6 relative">
      <BiometricBackground />
      <div className="relative z-10 w-full flex flex-col items-center">
      <h1 className="text-4xl font-bold tracking-widest text-lynx-primary mb-1 lynx-glow">
        LYNX
      </h1>
      <p className="text-lynx-text/60 mb-8 text-sm">Crea tu identidad biométrica</p>

      <motion.div
        initial={{ opacity: 0, y: 24, scale: 0.98 }}
        animate={{ opacity: 1, y: 0, scale: 1 }}
        transition={{ duration: 0.5, ease: 'easeOut' }}
        className="w-full max-w-xl"
      >
      <Card className="w-full flex flex-col items-center gap-6">
        <div className="flex gap-2 text-xs text-lynx-text/50">
          <span className={paso === 'datos' ? 'text-lynx-primary' : ''}>1. Datos</span>·
          <span className={paso === 'rostro' ? 'text-lynx-primary' : ''}>2. Rostro</span>·
          <span className={paso === 'voz' ? 'text-lynx-primary' : ''}>3. Voz</span>
        </div>

        {error && <Alert tipo="error" mensaje={error} />}

        {paso === 'datos' && (
          <div className="w-full max-w-sm space-y-3">
            <PasoHeader
              numero={1}
              total={3}
              titulo="Tus datos"
              descripcion="Completa tus datos básicos. No se pide contraseña: tu identidad será tu cara y tu voz."
            />
            <div>
              <input
                className={`w-full bg-lynx-surface border rounded-xl px-4 py-2.5 outline-none ${
                  errores.nombre ? 'border-lynx-error' : 'border-lynx-primary/30 focus:border-lynx-primary'
                }`}
                placeholder="Nombre completo"
                value={nombre}
                maxLength={60}
                onChange={(e) => {
                  setNombre(e.target.value)
                  if (errores.nombre) setErrores((p) => ({ ...p, nombre: undefined }))
                }}
              />
              {errores.nombre && <p className="text-xs text-lynx-error mt-1 px-1">{errores.nombre}</p>}
            </div>

            <div>
              <input
                className={`w-full bg-lynx-surface border rounded-xl px-4 py-2.5 outline-none ${
                  errores.email ? 'border-lynx-error' : 'border-lynx-primary/30 focus:border-lynx-primary'
                }`}
                placeholder="Email"
                type="email"
                value={email}
                onChange={(e) => {
                  setEmail(e.target.value)
                  if (errores.email) setErrores((p) => ({ ...p, email: undefined }))
                }}
              />
              {errores.email && <p className="text-xs text-lynx-error mt-1 px-1">{errores.email}</p>}
            </div>

            <div>
              <SearchableSelect
                options={DEPARTAMENTOS}
                value={departamento}
                onChange={(v) => {
                  setDepartamento(v)
                  if (errores.departamento) setErrores((p) => ({ ...p, departamento: undefined }))
                }}
                placeholder="Departamento (escribe para buscar)"
              />
              {errores.departamento && (
                <p className="text-xs text-lynx-error mt-1 px-1">{errores.departamento}</p>
              )}
            </div>

            <p className="text-xs text-lynx-text/50 px-1">
              Te registramos como <span className="text-lynx-primary">Empleado</span>.
              El administrador puede cambiar tu rol después.
            </p>
            <Button className="w-full" onClick={irARostro} disabled={cargando}>
              {cargando ? <Spinner size={18} /> : 'Siguiente →'}
            </Button>
          </div>
        )}

        {paso === 'rostro' && (
          <>
            <PasoHeader
              numero={2}
              total={3}
              titulo="Registro facial"
              descripcion="Coloca tu rostro dentro del óvalo. El recuadro se pondrá verde cuando estés bien posicionado y quieto; entonces captura tu foto."
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

            {!fotoPreview ? (
              <>
                <FaceCapture
                  videoRef={face.videoRef}
                  estado={face.estado}
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
                  onClick={capturarRostro}
                  disabled={cargando || !face.modelsLoaded || !face.listoParaCapturar || !face.camaraActiva}
                >
                  {cargando ? <Spinner size={18} /> : 'Capturar foto'}
                </Button>
                {!face.listoParaCapturar && face.modelsLoaded && (
                  <p className="text-xs text-lynx-warning text-center">
                    El botón se activa cuando el recuadro esté en verde.
                  </p>
                )}
                <button onClick={cancelarRegistro} className="text-xs text-lynx-text/50 hover:text-lynx-error">
                  Cancelar registro
                </button>
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
                  style={{ transform: 'scaleX(-1)' }}
                />
                <div className="flex gap-3">
                  <Button variant="ghost" onClick={repetirRostro} disabled={cargando}>
                    Repetir foto
                  </Button>
                  <Button onClick={confirmarRostro} disabled={cargando}>
                    {cargando ? <Spinner size={18} /> : 'Usar esta foto y continuar'}
                  </Button>
                </div>
                <button onClick={cancelarRegistro} className="text-xs text-lynx-text/50 hover:text-lynx-error">
                  Cancelar registro
                </button>
              </>
            )}
          </>
        )}

        {paso === 'voz' && (
          <>
            <PasoHeader
              numero={3}
              total={3}
              titulo="Registro de voz"
              descripcion="Pulsa Grabar y lee la frase en voz alta, clara y completa. Luego escúchate; si no quedó bien, regrábala antes de completar."
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
              <Button onClick={completarRegistro} disabled={!voz.audioBlob || cargando}>
                {cargando ? <Spinner size={18} /> : 'Completar registro'}
              </Button>
            </div>
            <button onClick={cancelarRegistro} className="text-xs text-lynx-text/50 hover:text-lynx-error">
              Cancelar registro
            </button>
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
      </motion.div>

      <Copyright />
      </div>
    </div>
  )
}
