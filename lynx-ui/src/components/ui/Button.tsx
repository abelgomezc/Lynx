/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
import type { ButtonHTMLAttributes } from 'react'

interface Props extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'ghost' | 'danger'
}

const estilos: Record<string, string> = {
  primary: 'bg-lynx-primary hover:opacity-90 text-white',
  secondary: 'bg-lynx-secondary hover:opacity-90 text-lynx-bg',
  ghost: 'bg-transparent border border-lynx-primary/40 hover:bg-lynx-primary/10 text-lynx-text',
  danger: 'bg-lynx-error hover:opacity-90 text-white',
}

export function Button({ variant = 'primary', className = '', ...props }: Props) {
  return (
    <button
      {...props}
      className={`px-5 py-2.5 rounded-xl font-medium transition disabled:opacity-50 disabled:cursor-not-allowed ${estilos[variant]} ${className}`}
    />
  )
}
