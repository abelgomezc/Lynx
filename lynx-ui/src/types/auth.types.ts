/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
export type Rol = 'ADMIN' | 'EMPLEADO' | 'SUPERVISOR'

export interface Usuario {
  id: number
  nombre: string
  email: string
  rol: Rol
  departamento?: string
  estadoRegistro: string
  esActivo: boolean
  fechaCreacion?: string
}

export interface AuthResponse {
  accessToken?: string
  refreshToken?: string
  usuario?: Usuario
  confianzaFacial?: number
  confianzaVoz?: number
  requiereSegundoFactor?: boolean
  frase?: string
  mensaje?: string
}

export interface RegistroRequest {
  nombre: string
  email: string
  rol?: Rol
  departamento?: string
}
