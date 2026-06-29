/*
 * Lynx - Paleta de identidad. © 2026 Abel Gomez.
 */
import type { Config } from 'tailwindcss'

export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        lynx: {
          primary: '#6C63FF',
          secondary: '#00D4AA',
          error: '#FF4A4A',
          warning: '#EF9F27',
          bg: '#0A0A14',
          surface: '#12121E',
          text: '#E8E8F8',
        },
      },
      fontFamily: {
        sans: ['Segoe UI', 'system-ui', 'Arial', 'sans-serif'],
      },
      keyframes: {
        'pulse-ring': {
          '0%': { boxShadow: '0 0 0 0 rgba(108,99,255,0.5)' },
          '70%': { boxShadow: '0 0 0 16px rgba(108,99,255,0)' },
          '100%': { boxShadow: '0 0 0 0 rgba(108,99,255,0)' },
        },
      },
      animation: {
        'pulse-ring': 'pulse-ring 1.8s infinite',
      },
    },
  },
  plugins: [],
} satisfies Config
