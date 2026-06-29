/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
import { useEffect, useState } from 'react'

interface Props {
  frase: string
  grabando: boolean
  duracion: number
  nivelAudio: number
  audioBlob?: Blob | null
}

export function VoiceCapture({ frase, grabando, duracion, nivelAudio, audioBlob }: Props) {
  // Visualizador de ondas (12 barras) según el nivel de audio
  const barras = Array.from({ length: 12 })

  // URL temporal para reproducir el audio grabado (escuchar antes de enviar)
  const [audioUrl, setAudioUrl] = useState<string | null>(null)
  useEffect(() => {
    if (audioBlob) {
      const url = URL.createObjectURL(audioBlob)
      setAudioUrl(url)
      return () => URL.revokeObjectURL(url)
    }
    setAudioUrl(null)
  }, [audioBlob])
  return (
    <div className="flex flex-col items-center gap-5 w-full max-w-md">
      <div className="glass p-6 w-full text-center">
        <p className="text-xs text-lynx-text/60 mb-2">Lee esta frase claramente:</p>
        <p className="text-lg font-semibold text-lynx-text">“{frase}”</p>
      </div>

      <div className="flex items-end gap-1.5 h-20">
        {barras.map((_, i) => {
          const altura = grabando
            ? Math.max(8, Math.min(80, nivelAudio * 80 * (0.5 + Math.random())))
            : 8
          return (
            <div
              key={i}
              className="w-2 rounded-full bg-lynx-secondary transition-all duration-100"
              style={{ height: `${altura}px` }}
            />
          )
        })}
      </div>

      <div className="flex items-center gap-2 text-sm">
        {grabando ? (
          <>
            <span className="w-2.5 h-2.5 rounded-full bg-lynx-error animate-pulse" />
            <span className="text-lynx-error">Grabando... {duracion}s</span>
          </>
        ) : (
          <span className="text-lynx-text/60">Micrófono listo</span>
        )}
      </div>

      {/* Reproductor para escuchar el audio antes de enviarlo */}
      {audioUrl && !grabando && (
        <div className="w-full text-center">
          <p className="text-xs text-lynx-text/60 mb-2">Escucha tu grabación antes de enviarla:</p>
          <audio controls src={audioUrl} className="w-full" />
        </div>
      )}
    </div>
  )
}
