"""
Lynx - voice-service - Extracción de características MFCC con librosa
© 2026 Abel Gomez. Todos los derechos reservados.
"""
from typing import List
import librosa
import numpy as np

import config


def extraer_voiceprint(audio_path: str) -> np.ndarray:
    """
    Carga el audio, calcula los MFCC y devuelve el voiceprint
    (promedio de cada coeficiente a lo largo del tiempo).
    """
    y, sr = librosa.load(audio_path, sr=config.AUDIO_SAMPLE_RATE)
    mfcc = librosa.feature.mfcc(y=y, sr=sr, n_mfcc=config.MFCC_N_COMPONENTS)
    return mfcc.mean(axis=1)


def voiceprint_a_lista(voiceprint: np.ndarray) -> List[float]:
    """Convierte el voiceprint numpy a una lista de floats serializable."""
    return [float(x) for x in voiceprint.tolist()]
