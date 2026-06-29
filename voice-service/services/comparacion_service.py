"""
Lynx - voice-service - Comparación de voiceprints (similitud coseno)
© 2026 Abel Gomez. Todos los derechos reservados.
"""
import numpy as np

import config


def similitud_coseno(actual: np.ndarray, registrado: np.ndarray) -> float:
    """Calcula la similitud coseno entre dos voiceprints."""
    denominador = np.linalg.norm(actual) * np.linalg.norm(registrado)
    if denominador == 0:
        return 0.0
    return float(np.dot(actual, registrado) / denominador)


def parsear_voiceprint(voiceprint_csv: str) -> np.ndarray:
    """Parsea un voiceprint en formato "v1,v2,..." o "[v1,v2,...]" a numpy."""
    limpio = voiceprint_csv.replace("[", "").replace("]", "").strip()
    return np.array([float(x) for x in limpio.split(",") if x.strip() != ""])


def voz_verificada(similitud: float) -> bool:
    """Decide si la similitud supera el umbral configurado."""
    return similitud > config.VOICE_SIMILITUD_UMBRAL
