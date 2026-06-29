/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
import { useAccessLive } from '../../hooks/useAccessLive'
import { Badge } from '../ui/Badge'

export function AccesosLive() {
  const { accesos, conectado } = useAccessLive()
  return (
    <div>
      <div className="flex items-center gap-2 mb-3">
        <span className={`w-2.5 h-2.5 rounded-full ${conectado ? 'bg-lynx-secondary' : 'bg-lynx-error'}`} />
        <span className="text-xs text-lynx-text/60">
          {conectado ? 'WebSocket conectado' : 'Desconectado'}
        </span>
      </div>
      {accesos.length === 0 ? (
        <p className="text-lynx-text/50 text-sm">Esperando accesos en tiempo real...</p>
      ) : (
        <div className="space-y-2">
          {accesos.map((a) => (
            <div key={`${a.id}-${a.fechaCreacion}`} className="flex items-center justify-between text-sm">
              <div className="flex items-center gap-2">
                <Badge color={a.resultado === 'EXITOSO' ? 'success' : 'error'}>{a.resultado}</Badge>
                <span className="text-lynx-text/80">{a.nombreUsuario ?? 'Desconocido'}</span>
              </div>
              <span className="text-lynx-text/40 text-xs">
                {a.ipAddress} · {a.pais ?? ''}
              </span>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
