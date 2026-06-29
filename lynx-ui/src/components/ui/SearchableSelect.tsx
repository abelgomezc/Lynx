/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 * Select con buscador (combobox): filtra opciones al escribir.
 */
import { useEffect, useState } from 'react'

interface Props {
  options: string[]
  value: string
  onChange: (valor: string) => void
  placeholder?: string
}

export function SearchableSelect({ options, value, onChange, placeholder }: Props) {
  const [open, setOpen] = useState(false)
  const [query, setQuery] = useState(value)

  useEffect(() => setQuery(value), [value])

  const filtradas = options.filter((o) =>
    o.toLowerCase().includes(query.toLowerCase())
  )

  return (
    <div className="relative w-full">
      <input
        className="w-full bg-lynx-surface border border-lynx-primary/30 rounded-xl px-4 py-2.5 outline-none focus:border-lynx-primary"
        placeholder={placeholder}
        value={query}
        onChange={(e) => {
          setQuery(e.target.value)
          onChange(e.target.value)
          setOpen(true)
        }}
        onFocus={() => setOpen(true)}
        onBlur={() => setTimeout(() => setOpen(false), 150)}
      />
      {open && filtradas.length > 0 && (
        <ul className="absolute z-20 mt-1 w-full max-h-52 overflow-auto rounded-xl border border-lynx-primary/30 bg-lynx-surface shadow-xl">
          {filtradas.map((o) => (
            <li
              key={o}
              onMouseDown={() => {
                onChange(o)
                setQuery(o)
                setOpen(false)
              }}
              className="px-4 py-2 text-sm cursor-pointer hover:bg-lynx-primary/20"
            >
              {o}
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}