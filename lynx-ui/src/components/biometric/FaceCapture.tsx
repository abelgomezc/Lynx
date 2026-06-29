/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
import type { RefObject } from 'react'

interface Props {
  videoRef: RefObject<HTMLVideoElement>
  rostroDetectado: boolean
  confianza: number
  instruccion?: string
  estado?: 'idle' | 'ok' | 'error'
}

export function FaceCapture({ videoRef, rostroDetectado, confianza, instruccion, estado = 'idle' }: Props) {
  const borde =
    estado === 'ok'
      ? 'border-lynx-secondary'
      : estado === 'error'
        ? 'border-lynx-error'
        : 'border-lynx-primary animate-pulse-ring'

  return (
    <div className="flex flex-col items-center gap-4">
      <div className={`relative rounded-2xl overflow-hidden border-4 ${borde}`}>
        <video
          ref={videoRef}
          autoPlay
          muted
          playsInline
          width={420}
          height={315}
          className="bg-black object-cover"
        />
      </div>
      {instruccion && (
        <p className="text-lynx-primary font-medium text-center">{instruccion}</p>
      )}
      <div className="w-full max-w-sm">
        <div className="flex justify-between text-xs mb-1 text-lynx-text/70">
          <span>{rostroDetectado ? 'Rostro detectado' : 'Buscando rostro...'}</span>
          <span>{(confianza * 100).toFixed(1)}%</span>
        </div>
        <div className="h-2 rounded-full bg-lynx-surface overflow-hidden">
          <div
            className="h-full bg-lynx-primary transition-all"
            style={{ width: `${Math.min(confianza * 100, 100)}%` }}
          />
        </div>
      </div>
    </div>
  )
}
