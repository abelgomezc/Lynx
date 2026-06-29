# Lynx · voice-service

> Reconocimiento y verificación de voz con **Whisper local** + MFCC (librosa).
> Parte del sistema **Lynx — Autenticación Biométrica Dual: Cara + Voz**.
> © 2026 Abel Gomez. Todos los derechos reservados.

Servicio Python (FastAPI) que transcribe la frase hablada y extrae un
voiceprint MFCC para verificar la identidad. Funciona 100% en local, sin
API keys ni conexión a la nube.

## Requisitos

- Python 3.10.11
- ffmpeg instalado en el sistema (Whisper lo necesita)

## Instalación

```bash
# 1) Crear y activar entorno virtual (opcional pero recomendado)
python -m venv .venv
# Windows
.venv\Scripts\activate
# Linux / Mac
source .venv/bin/activate

# 2) Instalar dependencias
pip install -r requirements.txt

# 3) Instalar ffmpeg
#   Windows (Chocolatey):  choco install ffmpeg
#   WSL/Ubuntu:            sudo apt install ffmpeg
#   Mac:                   brew install ffmpeg
```

## Ejecutar

```bash
python main.py
```

El servicio queda escuchando en `http://localhost:8083`.
La primera ejecución descarga el modelo Whisper `base` (una sola vez).

## Endpoints

| Método | Ruta             | Descripción                                  |
|--------|------------------|----------------------------------------------|
| POST   | `/voice/register`| Transcribe la frase y genera el voiceprint   |
| POST   | `/voice/verify`  | Verifica transcripción + voiceprint          |
| GET    | `/health`        | Estado del servicio y modelo                 |

### `/voice/register` (multipart/form-data)
- `audio`: archivo de audio (wav/webm)
- `frase_esperada`: texto que se pidió leer
- `id_usuario`: id del usuario

### `/voice/verify` (multipart/form-data)
- `audio`: archivo de audio
- `frase_esperada`: frase aleatoria mostrada (anti-replay)
- `voiceprint_registrado`: voiceprint guardado, como `v1,v2,...`
