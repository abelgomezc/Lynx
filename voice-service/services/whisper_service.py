"""
Lynx - voice-service - Transcripción con Whisper local
© 2026 Abel Gomez. Todos los derechos reservados.
"""
import whisper

import config

# El modelo se carga una sola vez al importar este módulo (en el arranque).
print(f"Cargando modelo Whisper '{config.WHISPER_MODEL}'...")
_modelo = whisper.load_model(config.WHISPER_MODEL)
print("Modelo Whisper cargado correctamente")


def transcribir(audio_path: str) -> str:
    """Transcribe el audio a texto en español, normalizado en minúsculas."""
    resultado = _modelo.transcribe(audio_path, language=config.IDIOMA)
    return resultado["text"].strip().lower()


def coincide_frase(transcripcion: str, frase_esperada: str) -> bool:
    """Compara la transcripción con la frase esperada (normalizadas)."""
    return transcripcion.strip().lower() == frase_esperada.strip().lower()
