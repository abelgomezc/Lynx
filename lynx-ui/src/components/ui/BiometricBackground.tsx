/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 * Fondo animado: esfera 3D de puntos (face-mesh / red neuronal) en
 * rotación, con líneas de conexión y partículas. Canvas puro, sin libs.
 */
import { useEffect, useRef } from 'react'

interface Punto3D {
  x: number
  y: number
  z: number
}

const NUM_PUNTOS = 150
const NUM_PARTICULAS = 60
const NUM_SIMBOLOS = 34
const COLOR_A = { r: 0x00, g: 0xd4, b: 0xaa } // cyan
const COLOR_B = { r: 0x6c, g: 0x63, b: 0xff } // morado

// Distribución uniforme en una esfera (espiral de Fibonacci)
function esferaFibonacci(n: number): Punto3D[] {
  const puntos: Punto3D[] = []
  const phi = Math.PI * (3 - Math.sqrt(5))
  for (let i = 0; i < n; i++) {
    const y = 1 - (i / (n - 1)) * 2
    const radio = Math.sqrt(1 - y * y)
    const theta = phi * i
    puntos.push({ x: Math.cos(theta) * radio, y, z: Math.sin(theta) * radio })
  }
  return puntos
}

function mezclar(t: number) {
  const r = Math.round(COLOR_A.r + (COLOR_B.r - COLOR_A.r) * t)
  const g = Math.round(COLOR_A.g + (COLOR_B.g - COLOR_A.g) * t)
  const b = Math.round(COLOR_A.b + (COLOR_B.b - COLOR_A.b) * t)
  return { r, g, b }
}

export function BiometricBackground() {
  const canvasRef = useRef<HTMLCanvasElement>(null)

  useEffect(() => {
    const canvas = canvasRef.current
    if (!canvas) return
    const ctx = canvas.getContext('2d')
    if (!ctx) return

    const puntos = esferaFibonacci(NUM_PUNTOS)
    const particulas = Array.from({ length: NUM_PARTICULAS }, () => ({
      x: Math.random(),
      y: Math.random(),
      vx: (Math.random() - 0.5) * 0.0006,
      vy: (Math.random() - 0.5) * 0.0006,
      r: Math.random() * 1.6 + 0.4,
    }))

    // Símbolos que ascienden lentamente por el fondo (binario + glifos)
    const GLIFOS = ['0', '1', '◇', '◎', '⊙', '∿', '⬡', '◈', '⌁', '10', '01']
    const simbolos = Array.from({ length: NUM_SIMBOLOS }, () => ({
      x: Math.random(),
      y: Math.random(),
      vel: Math.random() * 0.0009 + 0.0003,
      deriva: (Math.random() - 0.5) * 0.0004,
      tam: Math.random() * 16 + 10,
      alpha: Math.random() * 0.22 + 0.06,
      glifo: GLIFOS[Math.floor(Math.random() * GLIFOS.length)],
      cyan: Math.random() > 0.5,
    }))

    let w = 0
    let h = 0
    let dpr = Math.min(window.devicePixelRatio || 1, 2)
    const redimensionar = () => {
      w = canvas.clientWidth
      h = canvas.clientHeight
      dpr = Math.min(window.devicePixelRatio || 1, 2)
      canvas.width = w * dpr
      canvas.height = h * dpr
      ctx.setTransform(dpr, 0, 0, dpr, 0, 0)
    }
    redimensionar()
    window.addEventListener('resize', redimensionar)

    let raf = 0
    let a = 0 // ángulo Y
    let b = 0 // ángulo X
    let t = 0 // tiempo (frames)

    const dibujar = () => {
      a += 0.0024
      b += 0.0011
      t += 1
      ctx.clearRect(0, 0, w, h)

      const cxg = w / 2
      const cyg = h / 2

      // --- Halo central que "late" (respiración) ---
      const pulso = 0.5 + 0.5 * Math.sin(t * 0.02)
      const rGlow = Math.min(w, h) * (0.28 + pulso * 0.06)
      const glow = ctx.createRadialGradient(cxg, cyg, 0, cxg, cyg, rGlow)
      glow.addColorStop(0, `rgba(108, 99, 255, ${0.1 + pulso * 0.06})`)
      glow.addColorStop(0.5, 'rgba(0, 212, 170, 0.04)')
      glow.addColorStop(1, 'rgba(10, 10, 20, 0)')
      ctx.fillStyle = glow
      ctx.fillRect(0, 0, w, h)

      // --- Línea de escaneo biométrico que barre verticalmente ---
      const periodo = 320
      const fase = (t % periodo) / periodo
      const scanY = fase * h
      const banda = ctx.createLinearGradient(0, scanY - 40, 0, scanY + 40)
      banda.addColorStop(0, 'rgba(0, 212, 170, 0)')
      banda.addColorStop(0.5, 'rgba(0, 212, 170, 0.12)')
      banda.addColorStop(1, 'rgba(0, 212, 170, 0)')
      ctx.fillStyle = banda
      ctx.fillRect(0, scanY - 40, w, 80)
      ctx.beginPath()
      ctx.moveTo(0, scanY)
      ctx.lineTo(w, scanY)
      ctx.strokeStyle = 'rgba(0, 212, 170, 0.35)'
      ctx.lineWidth = 1
      ctx.stroke()

      // --- Partículas de fondo ---
      for (const p of particulas) {
        p.x += p.vx
        p.y += p.vy
        if (p.x < 0 || p.x > 1) p.vx *= -1
        if (p.y < 0 || p.y > 1) p.vy *= -1
        ctx.beginPath()
        ctx.arc(p.x * w, p.y * h, p.r, 0, Math.PI * 2)
        ctx.fillStyle = 'rgba(108, 99, 255, 0.25)'
        ctx.fill()
      }

      // --- Símbolos ascendiendo (binario + glifos) ---
      ctx.textAlign = 'center'
      ctx.textBaseline = 'middle'
      for (const s of simbolos) {
        s.y -= s.vel
        s.x += s.deriva
        if (s.y < -0.05) {
          s.y = 1.05
          s.x = Math.random()
          s.glifo = GLIFOS[Math.floor(Math.random() * GLIFOS.length)]
        }
        if (s.x < 0) s.x = 1
        if (s.x > 1) s.x = 0
        ctx.font = `${s.tam}px "Segoe UI", monospace`
        ctx.fillStyle = s.cyan
          ? `rgba(0, 212, 170, ${s.alpha})`
          : `rgba(108, 99, 255, ${s.alpha})`
        ctx.fillText(s.glifo, s.x * w, s.y * h)
      }

      // --- Esfera 3D de puntos ---
      const cx = w / 2
      const cy = h / 2
      const radio = Math.min(w, h) * (0.31 + pulso * 0.02) // respiración sutil
      const fov = 3

      const sa = Math.sin(a)
      const ca = Math.cos(a)
      const sb = Math.sin(b)
      const cb = Math.cos(b)

      const proyectados = puntos.map((pt) => {
        // rotación Y
        const x1 = pt.x * ca + pt.z * sa
        const z1 = -pt.x * sa + pt.z * ca
        const y1 = pt.y
        // rotación X
        const y2 = y1 * cb - z1 * sb
        const z2 = y1 * sb + z1 * cb
        const escala = fov / (fov + z2)
        return {
          sx: cx + x1 * radio * escala,
          sy: cy + y2 * radio * escala,
          z: z2,
          escala,
        }
      })

      // Líneas de conexión entre puntos cercanos (efecto red)
      const umbral = radio * 0.42
      for (let i = 0; i < proyectados.length; i++) {
        for (let j = i + 1; j < proyectados.length; j++) {
          const dx = proyectados[i].sx - proyectados[j].sx
          const dy = proyectados[i].sy - proyectados[j].sy
          const d = Math.hypot(dx, dy)
          if (d < umbral) {
            const alpha = (1 - d / umbral) * 0.18
            ctx.beginPath()
            ctx.moveTo(proyectados[i].sx, proyectados[i].sy)
            ctx.lineTo(proyectados[j].sx, proyectados[j].sy)
            ctx.strokeStyle = `rgba(108, 99, 255, ${alpha})`
            ctx.lineWidth = 0.6
            ctx.stroke()
          }
        }
      }

      // Puntos (color y tamaño según profundidad)
      for (const p of proyectados) {
        const t = (p.z + 1) / 2 // 0 (frente) .. 1 (fondo)
        const { r, g, b: bb } = mezclar(t)
        const alpha = 0.35 + (1 - t) * 0.55
        const tam = (1 - t) * 2.2 + 0.8
        ctx.beginPath()
        ctx.arc(p.sx, p.sy, tam, 0, Math.PI * 2)
        ctx.fillStyle = `rgba(${r}, ${g}, ${bb}, ${alpha})`
        ctx.fill()
      }

      raf = requestAnimationFrame(dibujar)
    }

    dibujar()

    return () => {
      cancelAnimationFrame(raf)
      window.removeEventListener('resize', redimensionar)
    }
  }, [])

  return (
    <canvas
      ref={canvasRef}
      className="fixed inset-0 h-full w-full"
      style={{ pointerEvents: 'none', zIndex: 0 }}
      aria-hidden="true"
    />
  )
}