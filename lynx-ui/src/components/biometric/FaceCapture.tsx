/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
import type { RefObject } from 'react'
import type { EstadoRostro } from '../../hooks/useFaceCapture'

interface Props {
  videoRef: RefObject<HTMLVideoElement>
  estado: EstadoRostro
  mensaje: string
  confianza: number
}

const COLORES: Record<EstadoRostro, { borde: string; ovalo: string; texto: string; icono: string }> = {
  neutral: { borde: 'border-lynx-primary/40', ovalo: '#6C63FF', texto: 'text-lynx-primary', icono: '○' },
  rojo: { borde: 'border-lynx-error', ovalo: '#FF4A4A', texto: 'text-lynx-error', icono: '✕' },
  amber: { borde: 'border-lynx-warning', ovalo: '#EF9F27', texto: 'text-lynx-warning', icono: '!' },
  verde: { borde: 'border-lynx-secondary', ovalo: '#00D4AA', texto: 'text-lynx-secondary', icono: '✓' },
}

const ANCHO = 420
const ALTO = 320

export function FaceCapture({ videoRef, estado, mensaje, confianza }: Props) {
  const c = COLORES[estado]
  return (
    <div className="flex flex-col items-center gap-4">
      <div className={`relative rounded-2xl overflow-hidden border-4 transition-colors ${c.borde}`}>
        <video
          ref={videoRef}
          autoPlay
          muted
          playsInline
          width={ANCHO}
          height={ALTO}
          className="bg-black object-cover"
          style={{ transform: 'scaleX(-1)' }}
        />
        {/* Óvalo guía: indica dónde colocar el rostro */}
        <svg
          className="pointer-events-none absolute inset-0"
          width={ANCHO}
          height={ALTO}
          viewBox={`0 0 ${ANCHO} ${ALTO}`}
        >
          <ellipse
            cx={ANCHO / 2}
            cy={ALTO / 2}
            rx={108}
            ry={140}
            fill="none"
            stroke={c.ovalo}
            strokeWidth={3}
            strokeDasharray="10 8"
            opacity={0.9}
          />
        </svg>
      </div>

      {/* Mensaje guía con icono de estado */}
      <div className={`flex items-center gap-2 font-medium ${c.texto}`}>
        <span className="text-lg">{c.icono}</span>
        <span className="text-center">{mensaje}</span>
      </div>

      {/* Barra de confianza */}
      <div className="w-full max-w-sm">
        <div className="flex justify-between text-xs mb-1 text-lynx-text/60">
          <span>Calidad de detección</span>
          <span>{(confianza * 100).toFixed(0)}%</span>
        </div>
        <div className="h-2 rounded-full bg-lynx-surface overflow-hidden">
          <div
            className="h-full transition-all"
            style={{ width: `${Math.min(confianza * 100, 100)}%`, background: c.ovalo }}
          />
        </div>
      </div>
    </div>
  )
}