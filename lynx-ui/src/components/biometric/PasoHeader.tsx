/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
interface Props {
  numero?: number
  total?: number
  titulo: string
  descripcion: string
}

export function PasoHeader({ numero, total, titulo, descripcion }: Props) {
  return (
    <div className="text-center">
      {numero != null && total != null && (
        <p className="text-xs text-lynx-primary mb-1">
          Paso {numero} de {total}
        </p>
      )}
      <h3 className="text-lg font-semibold text-lynx-text">{titulo}</h3>
      <p className="text-sm text-lynx-text/60 mt-1">{descripcion}</p>
    </div>
  )
}