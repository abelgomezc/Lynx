/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
import type { AccionLiveness } from '../../utils/livenessUtils'

interface Props {
  accion: AccionLiveness
  restante: number
  midiendo?: boolean
}

export function LivenessCheck({ accion, restante, midiendo }: Props) {
  return (
    <div className="glass p-5 text-center w-full max-w-sm">
      <p className="text-xs text-lynx-text/60 mb-1">Prueba de vida (anti-spoofing)</p>
      <p className="text-lynx-primary font-semibold text-lg">{accion.texto}</p>
      <p className="text-lynx-warning text-sm mt-2">
        {midiendo ? 'Analizando tu movimiento...' : `Tienes ${restante}s`}
      </p>
    </div>
  )
}