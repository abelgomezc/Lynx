/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
import type { LogAcceso } from '../../types/access.types'
import { Badge } from '../ui/Badge'

export function AccesosList({ accesos }: { accesos: LogAcceso[] }) {
  if (!accesos.length) {
    return <p className="text-lynx-text/50 text-sm">Sin accesos registrados.</p>
  }
  return (
    <div className="space-y-2">
      {accesos.map((a) => (
        <div
          key={a.id}
          className="flex items-center justify-between text-sm py-2 border-b border-lynx-primary/10"
        >
          <div className="flex items-center gap-3">
            <Badge color={a.resultado === 'EXITOSO' ? 'success' : 'error'}>
              {a.resultado}
            </Badge>
            <span className="text-lynx-text/80">{a.nombreUsuario ?? `Usuario ${a.idUsuario ?? '?'}`}</span>
          </div>
          <div className="flex items-center gap-3 text-lynx-text/50 text-xs">
            <span>{a.dispositivo ?? '—'}</span>
            <span>{a.ipAddress}</span>
            <span>{a.pais ?? ''}</span>
            <span>{a.fechaCreacion ? new Date(a.fechaCreacion).toLocaleString() : ''}</span>
          </div>
        </div>
      ))}
    </div>
  )
}
