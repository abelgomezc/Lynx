"""
Lynx - voice-service - Esquemas Pydantic (DTOs)
© 2026 Abel Gomez. Todos los derechos reservados.
"""
from typing import List
from pydantic import BaseModel


class RegistroVozResponse(BaseModel):
    id_usuario: int
    texto_correcto: bool
    transcripcion: str
    voiceprint: List[float]
    exitoso: bool


class VerificacionVozResponse(BaseModel):
    texto_correcto: bool
    voz_verificada: bool
    transcripcion: str
    similitud_voz: float
    confianza: float
    exitoso: bool


class HealthResponse(BaseModel):
    status: str
    servicio: str
    modelo: str
