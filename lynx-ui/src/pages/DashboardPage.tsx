/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
import { useQuery } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { accessApi } from '../api/accessApi'
import { authApi } from '../api/authApi'
import { useAuthStore } from '../store/authStore'
import { AccesosList } from '../components/dashboard/AccesosList'
import { Card } from '../components/ui/Card'
import { Button } from '../components/ui/Button'
import { Copyright } from '../components/ui/Copyright'
import { Spinner } from '../components/ui/Spinner'

export function DashboardPage() {
  const navigate = useNavigate()
  const { usuario, refreshToken, logout } = useAuthStore()

  const { data: historial, isLoading } = useQuery({
    queryKey: ['mi-historial'],
    queryFn: accessApi.miHistorial,
  })

  const salir = async () => {
    if (refreshToken) await authApi.logout(refreshToken).catch(() => {})
    logout()
    navigate('/login')
  }

  const ultimo = historial?.[0]

  return (
    <div className="min-h-screen p-6 max-w-5xl mx-auto">
      <header className="flex items-center justify-between mb-8">
        <h1 className="text-2xl font-bold tracking-widest text-lynx-primary">LYNX</h1>
        <div className="flex items-center gap-4">
          <span className="text-sm text-lynx-text/80">{usuario?.nombre}</span>
          <Button variant="ghost" className="!px-3 !py-1.5 text-sm" onClick={salir}>
            Salir
          </Button>
        </div>
      </header>

      <Card className="mb-6">
        <h2 className="text-xl font-semibold">Bienvenido de vuelta, {usuario?.nombre}</h2>
        {ultimo && (
          <p className="text-sm text-lynx-text/60 mt-1">
            Último acceso: {ultimo.fechaCreacion ? new Date(ultimo.fechaCreacion).toLocaleString() : '—'} ·{' '}
            {ultimo.pais ?? ''} · {ultimo.dispositivo ?? ''}
          </p>
        )}
        <p className="text-sm text-lynx-secondary mt-1">
          Departamento: {usuario?.departamento ?? '—'} · Rol: {usuario?.rol}
        </p>
      </Card>

      <Card>
        <div className="flex items-center justify-between mb-4">
          <h3 className="font-semibold">Mis últimos accesos</h3>
        </div>
        {isLoading ? <Spinner /> : <AccesosList accesos={historial ?? []} />}
      </Card>

      <Copyright />
    </div>
  )
}
