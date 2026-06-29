/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
export function Spinner({ size = 24 }: { size?: number }) {
  return (
    <div
      className="inline-block animate-spin rounded-full border-2 border-lynx-primary border-t-transparent"
      style={{ width: size, height: size }}
      role="status"
      aria-label="Cargando"
    />
  )
}
