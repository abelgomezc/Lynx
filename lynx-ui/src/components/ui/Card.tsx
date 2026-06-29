/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
import type { ReactNode } from 'react'

interface Props {
  children: ReactNode
  className?: string
}

export function Card({ children, className = '' }: Props) {
  return <div className={`glass p-6 ${className}`}>{children}</div>
}
