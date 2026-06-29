/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
export interface LogAcceso {
  id: number
  idUsuario?: number
  nombreUsuario?: string
  ipAddress: string
  pais?: string
  ciudad?: string
  dispositivo?: string
  resultado: 'EXITOSO' | 'FALLIDO' | 'BLOQUEADO' | 'SPOOFING'
  factor1Exitoso?: boolean
  factor2Exitoso?: boolean
  factorFallido?: string
  confianzaFacial?: number
  confianzaVoz?: number
  esSpoofing?: boolean
  fechaCreacion?: string
}

export interface Alerta {
  id: number
  tipo: string
  descripcion: string
  idUsuario?: number
  ipAddress?: string
  fotoEvidencia?: string
  resuelta: boolean
  fechaCreacion?: string
}

export interface Metricas {
  accesosHoy: number
  exitososHoy: number
  fallidosHoy: number
  alertasActivas: number
  spoofingHoy: number
}
