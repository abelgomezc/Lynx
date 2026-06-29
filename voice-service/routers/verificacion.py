"""
Lynx - voice-service - Router de verificación de voz
© 2026 Abel Gomez. Todos los derechos reservados.
"""
from fastapi import APIRouter, File, Form, HTTPException, UploadFile

from models.schemas import VerificacionVozResponse
from services import comparacion_service, mfcc_service, whisper_service
from utils import audio_utils

router = APIRouter(prefix="/voice", tags=["verificacion"])


@router.post("/verify", response_model=VerificacionVozResponse)
async def verificar_voz(
    audio: UploadFile = File(...),
    frase_esperada: str = Form(...),
    voiceprint_registrado: str = Form(...),
):
    """Verifica transcripción (anti-replay) y voiceprint (similitud coseno)."""
    audio_path = await audio_utils.guardar_audio_temporal(audio)
    with audio_utils.limpiar_al_terminar(audio_path):
        try:
            audio_utils.validar_duracion(audio_path)
        except ValueError as exc:
            raise HTTPException(status_code=400, detail=str(exc))

        transcripcion = whisper_service.transcribir(audio_path)
        texto_correcto = whisper_service.coincide_frase(transcripcion, frase_esperada)

        voiceprint_actual = mfcc_service.extraer_voiceprint(audio_path)
        voiceprint_ref = comparacion_service.parsear_voiceprint(voiceprint_registrado)

        similitud = comparacion_service.similitud_coseno(voiceprint_actual, voiceprint_ref)
        voz_ok = comparacion_service.voz_verificada(similitud)

        exitoso = texto_correcto and voz_ok
        confianza = similitud if exitoso else 0.0

        return VerificacionVozResponse(
            texto_correcto=texto_correcto,
            voz_verificada=voz_ok,
            transcripcion=transcripcion,
            similitud_voz=round(similitud, 4),
            confianza=round(confianza, 4),
            exitoso=exitoso,
        )
