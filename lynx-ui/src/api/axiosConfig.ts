/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 * Axios con timeout 30s + interceptor de refresh automático en 401.
 */
import axios from 'axios'
import { useAuthStore } from '../store/authStore'

const api = axios.create({
  baseURL: '/api',
  timeout: 30000,
})

// Adjunta el access token en cada petición
api.interceptors.request.use((config) => {
  const token = useAuthStore.getState().accessToken
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

let refrescando = false

// Renueva el token automáticamente al recibir 401
api.interceptors.response.use(
  (res) => res,
  async (error) => {
    const original = error.config
    const { refreshToken, setTokens, logout } = useAuthStore.getState()

    if (error.response?.status === 401 && !original._reintento && refreshToken && !refrescando) {
      original._reintento = true
      refrescando = true
      try {
        const { data } = await axios.post('/api/auth/refresh', { refreshToken })
        setTokens(data.accessToken, data.refreshToken)
        refrescando = false
        original.headers.Authorization = `Bearer ${data.accessToken}`
        return api(original)
      } catch (e) {
        refrescando = false
        logout()
        return Promise.reject(e)
      }
    }
    return Promise.reject(error)
  }
)

export default api
