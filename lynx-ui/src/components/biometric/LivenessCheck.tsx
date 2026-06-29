/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
interface Props {
  accion: string
  restante: number
}

export function LivenessCheck({ accion, restante }: Props) {
  return (
    <div className="glass p-5 text-center w-full max-w-sm">
      <p className="text-xs text-lynx-text/60 mb-1">Prueba de vida (anti-spoofing)</p>
      <p className="text-lynx-primary font-semibold text-lg">{accion}</p>
      <p className="text-lynx-warning text-sm mt-2">Tienes {restante}s</p>
    </div>
  )
}
