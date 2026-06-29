/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
interface Props {
  tipo?: 'info' | 'success' | 'error' | 'warning'
  mensaje: string
}

const estilos: Record<string, string> = {
  info: 'bg-lynx-primary/15 border-lynx-primary/40 text-lynx-text',
  success: 'bg-lynx-secondary/15 border-lynx-secondary/40 text-lynx-secondary',
  error: 'bg-lynx-error/15 border-lynx-error/40 text-lynx-error',
  warning: 'bg-lynx-warning/15 border-lynx-warning/40 text-lynx-warning',
}

export function Alert({ tipo = 'info', mensaje }: Props) {
  return (
    <div className={`px-4 py-3 rounded-xl border text-sm ${estilos[tipo]}`}>{mensaje}</div>
  )
}
