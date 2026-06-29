/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
import type { ReactNode } from 'react'

interface Props {
  children: ReactNode
  color?: 'primary' | 'success' | 'error' | 'warning'
}

const estilos: Record<string, string> = {
  primary: 'bg-lynx-primary/20 text-lynx-primary',
  success: 'bg-lynx-secondary/20 text-lynx-secondary',
  error: 'bg-lynx-error/20 text-lynx-error',
  warning: 'bg-lynx-warning/20 text-lynx-warning',
}

export function Badge({ children, color = 'primary' }: Props) {
  return (
    <span className={`px-2.5 py-1 rounded-full text-xs font-semibold ${estilos[color]}`}>
      {children}
    </span>
  )
}
