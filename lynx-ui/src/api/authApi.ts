/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
import api from './axiosConfig'
import type { AuthResponse, RegistroRequest, Usuario } from '../types/auth.types'

export const authApi = {
  registrar: (datos: RegistroRequest) =>
    api.post<Usuario>('/auth/register', datos).then((r) => r.data),

  registrarRostro: (idUsuario: number, embedding: number[], fotoReferencia?: string) =>
    api
      .post<Usuario>('/auth/register/face', { idUsuario, embedding, fotoReferencia })
      .then((r) => r.data),

  registrarVoz: (audio: Blob, fraseEsperada: string, idUsuario: number) => {
    const form = new FormData()
    form.append('audio', audio, 'voz.wav')
    form.append('fraseEsperada', fraseEsperada)
    form.append('idUsuario', String(idUsuario))
    return api.post<Usuario>('/auth/register/voice', form).then((r) => r.data)
  },

  // Registro ATÓMICO: envía datos + rostro + voz en una sola petición.
  registrarCompleto: (
    datos: RegistroRequest,
    embedding: number[],
    audio: Blob,
    fraseEsperada: string,
    fotoReferencia?: string
  ) => {
    const form = new FormData()
    form.append('nombre', datos.nombre)
    form.append('email', datos.email)
    if (datos.departamento) form.append('departamento', datos.departamento)
    form.append('embedding', JSON.stringify(embedding))
    if (fotoReferencia) form.append('fotoReferencia', fotoReferencia)
    form.append('audio', audio, 'voz.wav')
    form.append('fraseEsperada', fraseEsperada)
    return api.post<Usuario>('/auth/register/complete', form).then((r) => r.data)
  },

  fraseRegistro: () =>
    api.get<{ frase: string }>('/auth/register/frase').then((r) => r.data.frase),

  emailDisponible: (email: string) =>
    api
      .get<{ disponible: boolean }>(`/auth/register/disponible?email=${encodeURIComponent(email)}`)
      .then((r) => r.data.disponible),

  loginFacial: (embedding: number[], ipAddress?: string, dispositivo?: string) =>
    api
      .post<AuthResponse>('/auth/login/face', { embedding, ipAddress, dispositivo })
      .then((r) => r.data),

  loginVoz: (
    audio: Blob,
    idUsuario: number,
    fraseEsperada: string,
    confianzaFacial?: number,
    dispositivo?: string
  ) => {
    const form = new FormData()
    form.append('audio', audio, 'voz.wav')
    form.append('idUsuario', String(idUsuario))
    form.append('fraseEsperada', fraseEsperada)
    if (confianzaFacial != null) form.append('confianzaFacial', String(confianzaFacial))
    if (dispositivo) form.append('dispositivo', dispositivo)
    return api.post<AuthResponse>('/auth/login/voice', form).then((r) => r.data)
  },

  generarFrase: (idUsuario: number) =>
    api.get<{ frase: string }>(`/auth/frase?idUsuario=${idUsuario}`).then((r) => r.data),

  me: () => api.get<Usuario>('/auth/me').then((r) => r.data),

  logout: (refreshToken: string) => api.post('/auth/logout', { refreshToken }),
}
