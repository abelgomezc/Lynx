"""
Lynx - voice-service - Router de registro de voz
© 2026 Abel Gomez. Todos los derechos reservados.
"""
from fastapi import APIRouter, File, Form, HTTPException, UploadFile

from models.schemas import RegistroVozResponse
from services import mfcc_service, whisper_service
from utils import audio_utils

router = APIRouter(prefix="/voice", tags=["registro"])


@router.post("/register", response_model=RegistroVozResponse)
async def registrar_voz(
    audio: UploadFile = File(...),
    frase_esperada: str = Form(...),
    id_usuario: int = Form(...),
):
    """Transcribe la frase, valida que coincida y extrae el voiceprint MFCC."""
    audio_path = await audio_utils.guardar_audio_temporal(audio)
    with audio_utils.limpiar_al_terminar(audio_path):
        try:
            audio_utils.validar_duracion(audio_path)
        except ValueError as exc:
            raise HTTPException(status_code=400, detail=str(exc))

        transcripcion = whisper_service.transcribir(audio_path)
        texto_correcto = whisper_service.coincide_frase(transcripcion, frase_esperada)

        voiceprint = mfcc_service.extraer_voiceprint(audio_path)

        return RegistroVozResponse(
            id_usuario=id_usuario,
            texto_correcto=texto_correcto,
            transcripcion=transcripcion,
            voiceprint=mfcc_service.voiceprint_a_lista(voiceprint),
            exitoso=texto_correcto,
        )
