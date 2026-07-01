/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 * Grabación de voz con MediaRecorder + nivel de audio (Web Audio API).
 */
import { useCallback, useRef, useState } from 'react'

export const useVoiceCapture = () => {
  const mediaRecorderRef = useRef<MediaRecorder | null>(null)
  const animFrameRef = useRef<number>()
  const timerRef = useRef<ReturnType<typeof setInterval>>()
  const [grabando, setGrabando] = useState(false)
  const [audioBlob, setAudioBlob] = useState<Blob | null>(null)
  const [duracion, setDuracion] = useState(0)
  const [nivelAudio, setNivelAudio] = useState(0)
  const [micActivo, setMicActivo] = useState(false)
  const [error, setError] = useState<string | null>(null)

  // Solicita/enciende el micrófono (dispara el prompt del navegador).
  const encenderMicrofono = useCallback(async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
      stream.getTracks().forEach((t) => t.stop()) // solo para pedir permiso
      setMicActivo(true)
      setError(null)
      return true
    } catch {
      setMicActivo(false)
      setError(
        'No se pudo acceder al micrófono. Si lo bloqueaste, actívalo desde el ' +
          'candado 🔒 de la barra de direcciones y pulsa "Encender micrófono".'
      )
      return false
    }
  }, [])

  const iniciarGrabacion = useCallback(async () => {
    setAudioBlob(null)
    let stream: MediaStream
    try {
      stream = await navigator.mediaDevices.getUserMedia({ audio: true })
      setMicActivo(true)
      setError(null)
    } catch {
      setMicActivo(false)
      setError(
        'No se pudo acceder al micrófono. Actívalo desde el candado 🔒 del ' +
          'navegador y pulsa "Encender micrófono".'
      )
      return
    }
    const mediaRecorder = new MediaRecorder(stream)
    const chunks: BlobPart[] = []

    const audioCtx = new AudioContext()
    const source = audioCtx.createMediaStreamSource(stream)
    const analyser = audioCtx.createAnalyser()
    analyser.fftSize = 256
    source.connect(analyser)
    const dataArray = new Uint8Array(analyser.frequencyBinCount)

    const updateLevel = () => {
      analyser.getByteFrequencyData(dataArray)
      const avg = dataArray.reduce((a, b) => a + b, 0) / dataArray.length
      setNivelAudio(avg / 128)
      animFrameRef.current = requestAnimationFrame(updateLevel)
    }
    updateLevel()

    mediaRecorder.ondataavailable = (e) => chunks.push(e.data)
    mediaRecorder.onstop = () => {
      setAudioBlob(new Blob(chunks, { type: 'audio/wav' }))
      if (animFrameRef.current) cancelAnimationFrame(animFrameRef.current)
      if (timerRef.current) clearInterval(timerRef.current)
      stream.getTracks().forEach((t) => t.stop())
      audioCtx.close()
      setNivelAudio(0)
    }

    mediaRecorderRef.current = mediaRecorder
    mediaRecorder.start()
    setGrabando(true)
    setDuracion(0)
    timerRef.current = setInterval(() => setDuracion((d) => d + 1), 1000)
  }, [])

  const detenerGrabacion = useCallback(() => {
    mediaRecorderRef.current?.stop()
    setGrabando(false)
  }, [])

  return {
    grabando,
    audioBlob,
    duracion,
    nivelAudio,
    micActivo,
    error,
    encenderMicrofono,
    iniciarGrabacion,
    detenerGrabacion,
  }
}
