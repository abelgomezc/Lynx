/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
import type { Usuario } from '../../types/auth.types'
import { Badge } from '../ui/Badge'
import { Button } from '../ui/Button'

interface Props {
  usuarios: Usuario[]
  onBloquear: (id: number) => void
  onDesbloquear: (id: number) => void
}

export function UsuariosList({ usuarios, onBloquear, onDesbloquear }: Props) {
  return (
    <div className="space-y-2">
      {usuarios.map((u) => (
        <div
          key={u.id}
          className="flex items-center justify-between text-sm py-2 border-b border-lynx-primary/10"
        >
          <div className="flex items-center gap-3">
            <span className="text-lynx-text/90">{u.nombre}</span>
            <Badge>{u.rol}</Badge>
            <span className="text-lynx-text/40 text-xs">{u.departamento ?? '—'}</span>
          </div>
          <div className="flex items-center gap-3">
            <Badge color={u.esActivo ? 'success' : 'error'}>
              {u.esActivo ? 'Activo' : 'Bloqueado'}
            </Badge>
            {u.esActivo ? (
              <Button variant="danger" className="!px-3 !py-1 text-xs" onClick={() => onBloquear(u.id)}>
                Bloquear
              </Button>
            ) : (
              <Button
                variant="secondary"
                className="!px-3 !py-1 text-xs"
                onClick={() => onDesbloquear(u.id)}
              >
                Desbloquear
              </Button>
            )}
          </div>
        </div>
      ))}
    </div>
  )
}
