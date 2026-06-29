# Modelos de face-api.js

> © 2026 Abel Gomez. Todos los derechos reservados.

Coloca aquí los pesos de los modelos de **face-api.js** que el frontend
carga desde `/models/face_api_models`:

- `ssd_mobilenetv1_model-weights_manifest.json` (+ shards)
- `face_landmark_68_model-weights_manifest.json` (+ shards)
- `face_recognition_model-weights_manifest.json` (+ shards)

Descárgalos del repositorio oficial:
<https://github.com/justadudewhohacks/face-api.js/tree/master/weights>

Copia todos los archivos de esas tres familias de modelos dentro de esta
carpeta. Sin ellos, la cámara funciona pero no se generan embeddings.
