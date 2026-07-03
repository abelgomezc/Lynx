/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 * Captura de rostro con face-api.js + análisis de calidad en vivo
 * (tamaño, centrado, estabilidad) y semáforo de estado para guiar al usuario.
 */
import { useCallback, useEffect, useRef, useState } from 'react'
import * as faceapi from 'face-api.js'
import { calcularMetricas, type MuestraLiveness } from '../utils/livenessUtils'

export type EstadoRostro = 'neutral' | 'rojo' | 'amber' | 'verde'

export const useFaceCapture = () => {
  const videoRef = useRef<HTMLVideoElement>(null)
  const centroPrevRef = useRef<{ x: number; y: number } | null>(null)
  const framesOkRef = useRef(0)

  const [modelsLoaded, setModelsLoaded] = useState(false)
  const [cargandoModelos, setCargandoModelos] = useState(true)
  const [embedding, setEmbedding] = useState<number[] | null>(null)
  const [confianza, setConfianza] = useState(0)
  const [rostroDetectado, setRostroDetectado] = useState(false)
  const [estado, setEstado] = useState<EstadoRostro>('neutral')
  const [mensaje, setMensaje] = useState('Iniciando cámara...')
  const [listoParaCapturar, setListoParaCapturar] = useState(false)
  const [camaraActiva, setCamaraActiva] = useState(false)
  const [error, setError] = useState<string | null>(null)

  // 1) Cargar los modelos de face-api.js
  useEffect(() => {
    const loadModels = async () => {
      try {
        const url = '/models/face_api_models'
        await faceapi.nets.ssdMobilenetv1.loadFromUri(url)
        await faceapi.nets.faceLandmark68Net.loadFromUri(url)
        await faceapi.nets.faceRecognitionNet.loadFromUri(url)
        setModelsLoaded(true)
        setCargandoModelos(false)
        setMensaje('Coloca tu rostro dentro del óvalo.')
      } catch {
        setCargandoModelos(false)
        setError(
          'No se pudieron cargar los modelos de reconocimiento facial. ' +
            'Verifica que estén en lynx-ui/public/models/face_api_models y recarga.'
        )
        setMensaje('Error al cargar los modelos.')
        setEstado('rojo')
      }
    }
    loadModels()
  }, [])

  const iniciarCamara = useCallback(async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({
        video: { width: 640, height: 480, facingMode: 'user' },
      })
      if (videoRef.current) videoRef.current.srcObject = stream
      setCamaraActiva(true)
      setError(null)
    } catch {
      setCamaraActiva(false)
      setError(
        'No se pudo acceder a la cámara. Si la bloqueaste, ábrela desde el ' +
          'candado 🔒 de la barra de direcciones y pulsa "Encender cámara".'
      )
      setEstado('rojo')
    }
  }, [])

  const detenerCamara = useCallback(() => {
    const stream = videoRef.current?.srcObject as MediaStream | null
    stream?.getTracks().forEach((t) => t.stop())
    if (videoRef.current) videoRef.current.srcObject = null
    framesOkRef.current = 0
    setCamaraActiva(false)
    setListoParaCapturar(false)
    setEstado('neutral')
  }, [])

  // 2) Análisis de calidad en vivo (tamaño, centrado, estabilidad)
  useEffect(() => {
    if (!modelsLoaded) return
    const opciones = new faceapi.SsdMobilenetv1Options({ minConfidence: 0.3 })
    const id = setInterval(async () => {
      const video = videoRef.current
      if (!video || video.readyState < 2 || !video.srcObject) return
      const vw = video.videoWidth || 640
      const vh = video.videoHeight || 480
      try {
        const det = await faceapi.detectSingleFace(video, opciones)

        if (!det) {
          framesOkRef.current = 0
          centroPrevRef.current = null
          setRostroDetectado(false)
          setConfianza(0)
          setListoParaCapturar(false)
          setEstado('rojo')
          setMensaje('No se detecta tu rostro. Colócate frente a la cámara.')
          return
        }

        const { x, y, width, height } = det.box
        const centro = { x: x + width / 2, y: y + height / 2 }
        const proporcion = width / vw // ~0.3–0.6 ideal
        const offsetX = Math.abs(centro.x - vw / 2) / vw
        const offsetY = Math.abs(centro.y - vh / 2) / vh

        // Movimiento respecto al frame anterior (estabilidad)
        let movimiento = 0
        if (centroPrevRef.current) {
          movimiento =
            (Math.abs(centro.x - centroPrevRef.current.x) +
              Math.abs(centro.y - centroPrevRef.current.y)) /
            vw
        }
        centroPrevRef.current = centro

        setRostroDetectado(true)
        setConfianza(det.score)

        // Reglas de calidad (de peor a mejor)
        if (proporcion < 0.22) {
          framesOkRef.current = 0
          setEstado('amber'); setListoParaCapturar(false)
          setMensaje('Estás lejos: acércate a la cámara.')
        } else if (proporcion > 0.7) {
          framesOkRef.current = 0
          setEstado('amber'); setListoParaCapturar(false)
          setMensaje('Estás muy cerca: aléjate un poco.')
        } else if (offsetX > 0.18 || offsetY > 0.2) {
          framesOkRef.current = 0
          setEstado('amber'); setListoParaCapturar(false)
          setMensaje('Centra tu rostro dentro del óvalo.')
        } else if (movimiento > 0.04) {
          framesOkRef.current = 0
          setEstado('amber'); setListoParaCapturar(false)
          setMensaje('Mantente quieto un momento...')
        } else if (det.score < 0.6) {
          framesOkRef.current = 0
          setEstado('amber'); setListoParaCapturar(false)
          setMensaje('Mejora la iluminación (luz de frente).')
        } else {
          // Frame bueno: exige varios seguidos para confirmar estabilidad
          framesOkRef.current += 1
          if (framesOkRef.current >= 2) {
            setEstado('verde'); setListoParaCapturar(true)
            setMensaje('¡Perfecto! Rostro centrado y estable.')
          } else {
            setEstado('amber'); setListoParaCapturar(false)
            setMensaje('Quieto... confirmando.')
          }
        }
      } catch {
        /* el frame puede no estar listo; se reintenta en el siguiente tick */
      }
    }, 500)
    return () => clearInterval(id)
  }, [modelsLoaded])

  // Mide la prueba de vida: muestrea landmarks durante 'duracionMs' y
  // devuelve la serie de métricas (EAR/MAR/yaw) para verificar en el backend.
  const medirLiveness = useCallback(
    async (duracionMs = 3500): Promise<MuestraLiveness[]> => {
      const muestras: MuestraLiveness[] = []
      if (!videoRef.current || !modelsLoaded) return muestras
      const inicio = Date.now()
      while (Date.now() - inicio < duracionMs) {
        try {
          const det = await faceapi
            .detectSingleFace(videoRef.current)
            .withFaceLandmarks()
          if (det) {
            muestras.push(calcularMetricas(det.landmarks.positions))
          }
        } catch {
          /* frame no listo */
        }
        await new Promise((r) => setTimeout(r, 160))
      }
      return muestras
    },
    [modelsLoaded]
  )

  // 3) Captura el embedding completo (al pulsar el botón)
  const capturarEmbedding = useCallback(async (): Promise<number[] | null> => {
    if (!videoRef.current || !modelsLoaded) return null
    const detection = await faceapi
      .detectSingleFace(videoRef.current)
      .withFaceLandmarks()
      .withFaceDescriptor()

    if (detection) {
      const emb = Array.from(detection.descriptor)
      setEmbedding(emb)
      setConfianza(detection.detection.score)
      setRostroDetectado(true)
      return emb
    }
    setRostroDetectado(false)
    setEstado('rojo')
    setMensaje('No se detectó un rostro al capturar. Inténtalo de nuevo.')
    return null
  }, [modelsLoaded])

  return {
    videoRef,
    modelsLoaded,
    cargandoModelos,
    embedding,
    confianza,
    rostroDetectado,
    estado,
    mensaje,
    listoParaCapturar,
    camaraActiva,
    error,
    iniciarCamara,
    detenerCamara,
    capturarEmbedding,
    medirLiveness,
  }
}