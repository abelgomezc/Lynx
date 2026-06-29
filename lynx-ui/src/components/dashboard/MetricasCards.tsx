/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
import type { Metricas } from '../../types/access.types'
import { Card } from '../ui/Card'

export function MetricasCards({ metricas }: { metricas: Metricas }) {
  const items = [
    { label: 'Accesos hoy', valor: metricas.accesosHoy, color: 'text-lynx-primary' },
    { label: 'Exitosos', valor: metricas.exitososHoy, color: 'text-lynx-secondary' },
    { label: 'Fallidos', valor: metricas.fallidosHoy, color: 'text-lynx-warning' },
    { label: 'Spoofing', valor: metricas.spoofingHoy, color: 'text-lynx-error' },
    { label: 'Alertas activas', valor: metricas.alertasActivas, color: 'text-lynx-error' },
  ]
  return (
    <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
      {items.map((it) => (
        <Card key={it.label} className="text-center py-4">
          <p className={`text-3xl font-bold ${it.color}`}>{it.valor}</p>
          <p className="text-xs text-lynx-text/60 mt-1">{it.label}</p>
        </Card>
      ))}
    </div>
  )
}
