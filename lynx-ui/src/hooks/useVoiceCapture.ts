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

  const iniciarGrabacion = useCallback(async () => {
    setAudioBlob(null)
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
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

  return { grabando, audioBlob, duracion, nivelAudio, iniciarGrabacion, detenerGrabacion }
}
