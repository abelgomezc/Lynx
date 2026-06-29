"""
Lynx - voice-service - Utilidades de audio
© 2026 Abel Gomez. Todos los derechos reservados.
"""
import os
import tempfile
from contextlib import contextmanager

import librosa

import config


async def guardar_audio_temporal(audio) -> str:
    """Guarda un UploadFile en un archivo temporal .wav y devuelve la ruta."""
    with tempfile.NamedTemporaryFile(suffix=".wav", delete=False) as tmp:
        tmp.write(await audio.read())
        return tmp.name


def validar_duracion(audio_path: str) -> None:
    """Valida que la duración del audio no exceda el máximo permitido."""
    duracion = librosa.get_duration(path=audio_path)
    if duracion > config.MAX_AUDIO_DURATION:
        raise ValueError(
            f"El audio dura {duracion:.1f}s y excede el máximo de "
            f"{config.MAX_AUDIO_DURATION}s"
        )


@contextmanager
def limpiar_al_terminar(audio_path: str):
    """Context manager que elimina el archivo temporal al finalizar."""
    try:
        yield audio_path
    finally:
        if os.path.exists(audio_path):
            os.unlink(audio_path)
