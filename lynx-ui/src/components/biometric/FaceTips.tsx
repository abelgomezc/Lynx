/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 * Bocetos / recomendaciones para una captura de rostro precisa.
 */
const HAZ = [
  { icono: '💡', texto: 'Luz de frente y pareja' },
  { icono: '🙂', texto: 'Mira a la cámara, gesto neutro' },
  { icono: '🎯', texto: 'Rostro centrado en el óvalo' },
  { icono: '🧍', texto: 'Una sola persona en cuadro' },
]

const EVITA = [
  { icono: '🕶️', texto: 'Gorra, lentes oscuros o mascarilla' },
  { icono: '🌗', texto: 'Contraluz o sombras fuertes' },
  { icono: '🏃', texto: 'Moverte al capturar' },
  { icono: '📸', texto: 'Fotos o pantallas (anti-spoofing)' },
]

export function FaceTips() {
  return (
    <div className="w-full grid grid-cols-2 gap-3 text-xs">
      <div className="glass p-3">
        <p className="text-lynx-secondary font-semibold mb-2">✓ Haz esto</p>
        <ul className="space-y-1.5">
          {HAZ.map((h) => (
            <li key={h.texto} className="flex items-center gap-2 text-lynx-text/80">
              <span>{h.icono}</span>
              <span>{h.texto}</span>
            </li>
          ))}
        </ul>
      </div>
      <div className="glass p-3">
        <p className="text-lynx-error font-semibold mb-2">✕ Evita</p>
        <ul className="space-y-1.5">
          {EVITA.map((e) => (
            <li key={e.texto} className="flex items-center gap-2 text-lynx-text/80">
              <span>{e.icono}</span>
              <span>{e.texto}</span>
            </li>
          ))}
        </ul>
      </div>
    </div>
  )
}