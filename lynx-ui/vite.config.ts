/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 */
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Proxy de /api hacia el api-gateway (localhost:8080)
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  },
})
