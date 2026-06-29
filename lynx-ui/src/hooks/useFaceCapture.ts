/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 * Captura de rostro y generación de embedding (128d) con face-api.js.
 * Incluye detección en vivo + mensajes guía para el usuario.
 */
import { useCallback, useEffect, useRef, useState } from 'react'
import * as faceapi from 'face-api.js'

export const useFaceCapture = () => {
  const videoRef = useRef<HTMLVideoElement>(null)

  const [modelsLoaded, setModelsLoaded] = useState(false)
  const [cargandoModelos, setCargandoModelos] = useState(true)
  const [embedding, setEmbedding] = useState<number[] | null>(null)
  const [confianza, setConfianza] = useState(0)
  const [rostroDetectado, setRostroDetectado] = useState(false)
  const [mensaje, setMensaje] = useState('Iniciando cámara...')
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
        setMensaje('Modelos listos. Centra tu rostro en el recuadro.')
      } catch {
        setCargandoModelos(false)
        setError(
          'No se pudieron cargar los modelos de reconocimiento facial. ' +
            'Verifica que estén en lynx-ui/public/models/face_api_models y recarga.'
        )
        setMensaje('Error al cargar los modelos.')
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
    } catch {
      setError('No se pudo acceder a la cámara. Revisa los permisos del navegador.')
    }
  }, [])

  const detenerCamara = useCallback(() => {
    const stream = videoRef.current?.srcObject as MediaStream | null
    stream?.getTracks().forEach((t) => t.stop())
  }, [])

  // 2) Detección en vivo: actualiza la guía mientras el usuario se coloca.
  //    Intervalo independiente del ciclo de la cámara (no se borra al
  //    parar la cámara) para evitar quedarse "buscando rostro" sin avanzar.
  useEffect(() => {
    if (!modelsLoaded) return
    const opciones = new faceapi.SsdMobilenetv1Options({ minConfidence: 0.3 })
    const id = setInterval(async () => {
      const video = videoRef.current
      if (!video || video.readyState < 2 || !video.srcObject) return
      try {
        const det = await faceapi.detectSingleFace(video, opciones)
        if (det) {
          const score = det.score
          setRostroDetectado(true)
          setConfianza(score)
          if (score > 0.8) setMensaje('Rostro bien detectado. Pulsa el botón.')
          else if (score > 0.5) setMensaje('Casi: acércate un poco y mira al frente.')
          else setMensaje('Mejora la luz y centra tu cara en el recuadro.')
        } else {
          setRostroDetectado(false)
          setConfianza(0)
          setMensaje('No se detecta tu rostro: céntrate y busca buena iluminación.')
        }
      } catch {
        /* el frame puede no estar listo; se reintenta en el siguiente tick */
      }
    }, 600)
    return () => clearInterval(id)
  }, [modelsLoaded])

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
    mensaje,
    error,
    iniciarCamara,
    detenerCamara,
    capturarEmbedding,
  }
}