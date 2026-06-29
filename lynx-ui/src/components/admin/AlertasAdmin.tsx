/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
import { useState } from 'react'
import type { Alerta } from '../../types/access.types'
import { Badge } from '../ui/Badge'
import { Button } from '../ui/Button'

export function AlertasAdmin({ alertas }: { alertas: Alerta[] }) {
  const [foto, setFoto] = useState<string | null>(null)

  if (!alertas.length) {
    return <p className="text-lynx-text/50 text-sm">Sin alertas activas.</p>
  }
  return (
    <div className="space-y-2">
      {alertas.map((al) => (
        <div key={al.id} className="flex items-start justify-between gap-3 text-sm py-2 border-b border-lynx-primary/10">
          <div className="flex items-start gap-3">
            <Badge color={al.tipo.includes('SPOOFING') ? 'error' : 'warning'}>{al.tipo}</Badge>
            <div>
              <p className="text-lynx-text/80">{al.descripcion}</p>
              <p className="text-lynx-text/40 text-xs">
                {al.ipAddress ?? ''} · {al.fechaCreacion ? new Date(al.fechaCreacion).toLocaleString() : ''}
              </p>
            </div>
          </div>
          {al.fotoEvidencia && (
            <Button variant="ghost" className="!px-3 !py-1 text-xs" onClick={() => setFoto(al.fotoEvidencia!)}>
              Ver foto
            </Button>
          )}
        </div>
      ))}

      {foto && (
        <div
          className="fixed inset-0 bg-black/70 flex items-center justify-center z-50 p-6"
          onClick={() => setFoto(null)}
        >
          <img src={foto} alt="Evidencia" className="max-h-[80vh] rounded-2xl border border-lynx-primary" />
        </div>
      )}
    </div>
  )
}
