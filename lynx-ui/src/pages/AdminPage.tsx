/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { accessApi, adminApi } from '../api/accessApi'
import { authApi } from '../api/authApi'
import { useAuthStore } from '../store/authStore'
import { MetricasCards } from '../components/dashboard/MetricasCards'
import { AccessChart } from '../components/dashboard/AccessChart'
import { AccesosLive } from '../components/admin/AccesosLive'
import { UsuariosList } from '../components/admin/UsuariosList'
import { AlertasAdmin } from '../components/admin/AlertasAdmin'
import { Card } from '../components/ui/Card'
import { Button } from '../components/ui/Button'
import { Spinner } from '../components/ui/Spinner'
import { Copyright } from '../components/ui/Copyright'
import type { Metricas } from '../types/access.types'

export function AdminPage() {
  const navigate = useNavigate()
  const qc = useQueryClient()
  const { usuario, refreshToken, logout } = useAuthStore()

  const metricas = useQuery({ queryKey: ['metricas'], queryFn: accessApi.metricas })
  const accesos = useQuery({ queryKey: ['accesos-todos'], queryFn: accessApi.todos })
  const alertas = useQuery({ queryKey: ['alertas'], queryFn: accessApi.alertas })
  const usuarios = useQuery({ queryKey: ['usuarios'], queryFn: adminApi.usuarios })

  const bloquear = useMutation({
    mutationFn: adminApi.bloquear,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['usuarios'] }),
  })
  const desbloquear = useMutation({
    mutationFn: adminApi.desbloquear,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['usuarios'] }),
  })

  const salir = async () => {
    if (refreshToken) await authApi.logout(refreshToken).catch(() => {})
    logout()
    navigate('/login')
  }

  const metricasVacias: Metricas = {
    accesosHoy: 0,
    exitososHoy: 0,
    fallidosHoy: 0,
    alertasActivas: 0,
    spoofingHoy: 0,
  }

  return (
    <div className="min-h-screen p-6 max-w-6xl mx-auto">
      <header className="flex items-center justify-between mb-8">
        <h1 className="text-2xl font-bold tracking-widest text-lynx-primary">
          LYNX <span className="text-sm text-lynx-text/50">Admin</span>
        </h1>
        <div className="flex items-center gap-4">
          <span className="text-sm text-lynx-text/80">{usuario?.nombre}</span>
          <Button variant="ghost" className="!px-3 !py-1.5 text-sm" onClick={salir}>
            Salir
          </Button>
        </div>
      </header>

      <div className="mb-6">
        <MetricasCards metricas={metricas.data ?? metricasVacias} />
      </div>

      <div className="grid md:grid-cols-2 gap-6 mb-6">
        <Card>
          <h3 className="font-semibold mb-3">Accesos en tiempo real</h3>
          <AccesosLive />
        </Card>
        <Card>
          <h3 className="font-semibold mb-3">Alertas activas</h3>
          {alertas.isLoading ? <Spinner /> : <AlertasAdmin alertas={alertas.data ?? []} />}
        </Card>
      </div>

      <Card className="mb-6">
        <h3 className="font-semibold mb-3">Gráfica de accesos de hoy</h3>
        <AccessChart accesos={accesos.data ?? []} />
      </Card>

      <Card>
        <h3 className="font-semibold mb-3">Usuarios</h3>
        {usuarios.isLoading ? (
          <Spinner />
        ) : (
          <UsuariosList
            usuarios={usuarios.data ?? []}
            onBloquear={(id) => bloquear.mutate(id)}
            onDesbloquear={(id) => desbloquear.mutate(id)}
          />
        )}
      </Card>

      <Copyright />
    </div>
  )
}
