/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
import {
  Bar,
  BarChart,
  CartesianGrid,
  Legend,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'
import type { LogAcceso } from '../../types/access.types'

/** Agrupa los accesos por hora y los grafica (exitosos vs fallidos). */
export function AccessChart({ accesos }: { accesos: LogAcceso[] }) {
  const porHora: Record<string, { hora: string; exitosos: number; fallidos: number }> = {}
  for (let h = 0; h < 24; h++) {
    const k = `${h}:00`
    porHora[k] = { hora: k, exitosos: 0, fallidos: 0 }
  }
  accesos.forEach((a) => {
    if (!a.fechaCreacion) return
    const h = new Date(a.fechaCreacion).getHours()
    const k = `${h}:00`
    if (a.resultado === 'EXITOSO') porHora[k].exitosos++
    else porHora[k].fallidos++
  })
  const data = Object.values(porHora)

  return (
    <ResponsiveContainer width="100%" height={260}>
      <BarChart data={data}>
        <CartesianGrid strokeDasharray="3 3" stroke="#23233a" />
        <XAxis dataKey="hora" stroke="#8a8aa8" fontSize={11} />
        <YAxis stroke="#8a8aa8" fontSize={11} allowDecimals={false} />
        <Tooltip
          contentStyle={{ background: '#12121E', border: '1px solid #6C63FF', borderRadius: 12 }}
        />
        <Legend />
        <Bar dataKey="exitosos" fill="#00D4AA" name="Exitosos" radius={[4, 4, 0, 0]} />
        <Bar dataKey="fallidos" fill="#FF4A4A" name="Fallidos" radius={[4, 4, 0, 0]} />
      </BarChart>
    </ResponsiveContainer>
  )
}
