/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 * Zustand en memoria (sin localStorage): seguridad primero.
 */
import { create } from 'zustand'
import type { Usuario } from '../types/auth.types'

interface AuthState {
  accessToken: string | null
  refreshToken: string | null
  usuario: Usuario | null
  setSesion: (accessToken: string, refreshToken: string, usuario: Usuario) => void
  setTokens: (accessToken: string, refreshToken: string) => void
  logout: () => void
}

export const useAuthStore = create<AuthState>((set) => ({
  accessToken: null,
  refreshToken: null,
  usuario: null,
  setSesion: (accessToken, refreshToken, usuario) =>
    set({ accessToken, refreshToken, usuario }),
  setTokens: (accessToken, refreshToken) => set({ accessToken, refreshToken }),
  logout: () => set({ accessToken: null, refreshToken: null, usuario: null }),
}))
