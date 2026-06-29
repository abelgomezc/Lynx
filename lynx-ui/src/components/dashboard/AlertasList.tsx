/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
import type { Alerta } from '../../types/access.types'
import { Badge } from '../ui/Badge'

export function AlertasList({ alertas }: { alertas: Alerta[] }) {
  if (!alertas.length) {
    return <p className="text-lynx-text/50 text-sm">Sin alertas activas.</p>
  }
  return (
    <div className="space-y-2">
      {alertas.map((al) => (
        <div key={al.id} className="flex items-start gap-3 text-sm py-2 border-b border-lynx-primary/10">
          <Badge color={al.tipo.includes('SPOOFING') ? 'error' : 'warning'}>{al.tipo}</Badge>
          <div className="flex-1">
            <p className="text-lynx-text/80">{al.descripcion}</p>
            <p className="text-lynx-text/40 text-xs">
              {al.ipAddress ?? ''} · {al.fechaCreacion ? new Date(al.fechaCreacion).toLocaleString() : ''}
            </p>
          </div>
        </div>
      ))}
    </div>
  )
}
