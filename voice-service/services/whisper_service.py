"""
Lynx - voice-service - Transcripción con Whisper local
© 2026 Abel Gomez. Todos los derechos reservados.
"""
import logging
import re
import unicodedata
from difflib import SequenceMatcher

import whisper

import config

logger = logging.getLogger("lynx.voice")
logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")

# El modelo se carga una sola vez al importar este módulo (en el arranque).
print(f"Cargando modelo Whisper '{config.WHISPER_MODEL}'...")
_modelo = whisper.load_model(config.WHISPER_MODEL)
print("Modelo Whisper cargado correctamente")

# Umbral de similitud para aceptar la frase (0..1)
UMBRAL_FRASE = 0.82


def transcribir(audio_path: str) -> str:
    """Transcribe el audio a texto en español."""
    resultado = _modelo.transcribe(audio_path, language=config.IDIOMA)
    texto = resultado["text"].strip()
    logger.info("Transcripción Whisper: %r", texto)
    return texto


def _normalizar(texto: str) -> str:
    """Minúsculas, sin acentos, sin puntuación y espacios colapsados."""
    texto = texto.strip().lower()
    texto = unicodedata.normalize("NFD", texto)
    texto = "".join(c for c in texto if unicodedata.category(c) != "Mn")
    texto = re.sub(r"[^a-z0-9 ]", " ", texto)
    return re.sub(r"\s+", " ", texto).strip()


def coincide_frase(transcripcion: str, frase_esperada: str) -> bool:
    """
    Compara de forma tolerante: ignora tildes, puntuación y mayúsculas,
    y acepta si la similitud supera el umbral (evita falsos negativos por
    pequeñas diferencias de transcripción de Whisper).
    """
    a = _normalizar(transcripcion)
    b = _normalizar(frase_esperada)
    if not a or not b:
        return False
    if a == b:
        return True
    ratio = SequenceMatcher(None, a, b).ratio()
    logger.info("Comparación frase: esperado=%r oido=%r similitud=%.3f", b, a, ratio)
    return ratio >= UMBRAL_FRASE