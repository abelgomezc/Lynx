/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
interface Props {
  caraOk: boolean | null
  vozOk: boolean | null
}

function Indicador({ label, estado }: { label: string; estado: boolean | null }) {
  const icono = estado === null ? '○' : estado ? '✓' : '✕'
  const color =
    estado === null ? 'text-lynx-text/40' : estado ? 'text-lynx-secondary' : 'text-lynx-error'
  return (
    <div className="flex items-center gap-2">
      <span className={`text-xl ${color}`}>{icono}</span>
      <span className="text-sm text-lynx-text/80">{label}</span>
    </div>
  )
}

export function BiometricStatus({ caraOk, vozOk }: Props) {
  return (
    <div className="flex gap-6 justify-center">
      <Indicador label="Cara" estado={caraOk} />
      <Indicador label="Voz" estado={vozOk} />
    </div>
  )
}
