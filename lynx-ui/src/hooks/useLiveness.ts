/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 * Genera una instrucción de liveness (con código para el backend) y un
 * countdown de verificación.
 */
import { useCallback, useEffect, useRef, useState } from 'react'
import { accionLivenessAleatoria, type AccionLiveness } from '../utils/livenessUtils'

export const useLiveness = (segundos = 5) => {
  const [accion, setAccion] = useState<AccionLiveness>(accionLivenessAleatoria())
  const [restante, setRestante] = useState(segundos)
  const [activo, setActivo] = useState(false)
  const [superado, setSuperado] = useState(false)
  const timerRef = useRef<ReturnType<typeof setInterval>>()

  const iniciar = useCallback(() => {
    setAccion(accionLivenessAleatoria())
    setRestante(segundos)
    setSuperado(false)
    setActivo(true)
  }, [segundos])

  const confirmar = useCallback(() => {
    setSuperado(true)
    setActivo(false)
  }, [])

  useEffect(() => {
    if (!activo) return
    timerRef.current = setInterval(() => {
      setRestante((r) => {
        if (r <= 1) {
          clearInterval(timerRef.current)
          setActivo(false)
          return 0
        }
        return r - 1
      })
    }, 1000)
    return () => clearInterval(timerRef.current)
  }, [activo])

  return { accion, restante, activo, superado, iniciar, confirmar }
}