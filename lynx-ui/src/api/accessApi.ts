/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
import api from './axiosConfig'
import type { Alerta, LogAcceso, Metricas } from '../types/access.types'
import type { Usuario } from '../types/auth.types'

export const accessApi = {
  miHistorial: () => api.get<LogAcceso[]>('/access/my-history').then((r) => r.data),
  todos: () => api.get<LogAcceso[]>('/access/all').then((r) => r.data),
  metricas: () => api.get<Metricas>('/access/metricas').then((r) => r.data),
  alertas: () => api.get<Alerta[]>('/access/alertas').then((r) => r.data),
}

export const adminApi = {
  usuarios: () => api.get<Usuario[]>('/admin/usuarios').then((r) => r.data),
  bloquear: (id: number) =>
    api.put<Usuario>(`/admin/usuarios/${id}/bloquear`).then((r) => r.data),
  desbloquear: (id: number) =>
    api.put<Usuario>(`/admin/usuarios/${id}/desbloquear`).then((r) => r.data),
}
