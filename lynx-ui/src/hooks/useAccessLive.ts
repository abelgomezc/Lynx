/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 * WebSocket de accesos en tiempo real (access-service :8084).
 */
import { useEffect, useRef, useState } from 'react'
import type { LogAcceso } from '../types/access.types'

const WS_URL = 'ws://localhost:8084/access/live'

export const useAccessLive = (max = 20) => {
  const [accesos, setAccesos] = useState<LogAcceso[]>([])
  const [conectado, setConectado] = useState(false)
  const wsRef = useRef<WebSocket | null>(null)

  useEffect(() => {
    const ws = new WebSocket(WS_URL)
    wsRef.current = ws

    ws.onopen = () => setConectado(true)
    ws.onclose = () => setConectado(false)
    ws.onmessage = (ev) => {
      try {
        const log: LogAcceso = JSON.parse(ev.data)
        setAccesos((prev) => [log, ...prev].slice(0, max))
      } catch {
        /* ignora mensajes no JSON */
      }
    }

    return () => ws.close()
  }, [max])

  return { accesos, conectado }
}
