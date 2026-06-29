"""
Lynx - voice-service - Aplicación FastAPI
Sistema de Autenticación Biométrica Dual: Cara + Voz
© 2026 Abel Gomez. Todos los derechos reservados.

Procesamiento de voz 100% local con Whisper (sin API key ni nube).
"""
import uvicorn
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

import config
from models.schemas import HealthResponse
from routers import registro, verificacion

app = FastAPI(
    title="Lynx Voice Service",
    description="Reconocimiento y verificación de voz con Whisper local",
    version="1.0.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=config.CORS_ORIGINS,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(registro.router)
app.include_router(verificacion.router)


@app.get("/health", response_model=HealthResponse)
async def health():
    return HealthResponse(
        status="UP",
        servicio="voice-service",
        modelo=f"whisper-{config.WHISPER_MODEL}",
    )


if __name__ == "__main__":
    uvicorn.run(app, host=config.HOST, port=config.PORT)
