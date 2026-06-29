/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
import { Navigate, Route, Routes } from 'react-router-dom'
import type { ReactElement } from 'react'
import { useAuthStore } from './store/authStore'
import { LoginPage } from './pages/LoginPage'
import { RegisterPage } from './pages/RegisterPage'
import { DashboardPage } from './pages/DashboardPage'
import { AdminPage } from './pages/AdminPage'

/** Ruta protegida: requiere sesión y, opcionalmente, rol ADMIN. */
function Protegida({ children, soloAdmin = false }: { children: JSX.Element; soloAdmin?: boolean }) {
  const usuario = useAuthStore((s) => s.usuario)
  const token = useAuthStore((s) => s.accessToken)
  if (!token || !usuario) return <Navigate to="/login" replace />
  if (soloAdmin && usuario.rol !== 'ADMIN') return <Navigate to="/dashboard" replace />
  return children
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route
        path="/dashboard"
        element={
          <Protegida>
            <DashboardPage />
          </Protegida>
        }
      />
      <Route
        path="/admin"
        element={
          <Protegida soloAdmin>
            <AdminPage />
          </Protegida>
        }
      />
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  )
}
