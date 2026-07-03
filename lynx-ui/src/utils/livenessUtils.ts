/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 * Métricas de prueba de vida a partir de los 68 landmarks de face-api.js.
 * EAR (parpadeo), MAR (boca) y un proxy de yaw (giro de cabeza).
 */
export interface Punto {
  x: number
  y: number
}

export interface MuestraLiveness {
  ear: number // Eye Aspect Ratio (parpadeo)
  mar: number // Mouth Aspect Ratio (boca abierta)
  yaw: number // giro horizontal de la cabeza (-1..1 aprox)
}

function dist(a: Punto, b: Punto): number {
  return Math.hypot(a.x - b.x, a.y - b.y)
}

function earOjo(p: Punto[], i1: number, i2: number, i3: number, i4: number, i5: number, i6: number): number {
  const a = dist(p[i2], p[i6])
  const b = dist(p[i3], p[i5])
  const c = dist(p[i1], p[i4])
  return c === 0 ? 0 : (a + b) / (2 * c)
}

/** Calcula EAR, MAR y yaw a partir de los 68 puntos del rostro. */
export function calcularMetricas(p: Punto[]): MuestraLiveness {
  // Ojo izquierdo 36-41, ojo derecho 42-47
  const earIzq = earOjo(p, 36, 37, 38, 39, 40, 41)
  const earDer = earOjo(p, 42, 43, 44, 45, 46, 47)
  const ear = (earIzq + earDer) / 2

  // Boca: vertical (51-57) sobre horizontal (48-54)
  const vertical = dist(p[51], p[57])
  const horizontal = dist(p[48], p[54])
  const mar = horizontal === 0 ? 0 : vertical / horizontal

  // Yaw: posición de la nariz (30) entre los extremos de los ojos (36, 45)
  const xNariz = p[30].x
  const xIzq = p[36].x
  const xDer = p[45].x
  const ancho = xDer - xIzq
  const yaw = ancho === 0 ? 0 : ((xNariz - xIzq) - (xDer - xNariz)) / ancho

  return { ear, mar, yaw }
}

/** Acciones de liveness: código (para el backend) + texto (para el usuario). */
export const ACCIONES_LIVENESS = [
  { codigo: 'PARPADEA', texto: 'Parpadea dos veces' },
  { codigo: 'ABRE_BOCA', texto: 'Abre la boca' },
  { codigo: 'GIRA_CABEZA', texto: 'Gira la cabeza a un lado' },
] as const

export type AccionLiveness = (typeof ACCIONES_LIVENESS)[number]

export function accionLivenessAleatoria(): AccionLiveness {
  return ACCIONES_LIVENESS[Math.floor(Math.random() * ACCIONES_LIVENESS.length)]
}