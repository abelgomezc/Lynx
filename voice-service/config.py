"""
Lynx - voice-service - Configuración
© 2026 Abel Gomez. Todos los derechos reservados.
"""

WHISPER_MODEL = "base"          # base corre bien en CPU con Python 3.10
AUDIO_SAMPLE_RATE = 16000       # Hz estándar para voz
MFCC_N_COMPONENTS = 13          # número de coeficientes MFCC
VOICE_SIMILITUD_UMBRAL = 0.35   # similitud coseno mínima para verificar
MAX_AUDIO_DURATION = 10         # segundos máximo por grabación
IDIOMA = "es"                   # idioma de transcripción
PORT = 8083
HOST = "0.0.0.0"

# Orígenes permitidos para CORS (el navegador no llama directo, pero se deja listo)
CORS_ORIGINS = ["http://localhost:8080", "http://localhost:5173"]
