/*
 * Lynx - © 2026 Abel Gomez. Todos los derechos reservados.
 * (Las acciones de liveness se movieron a livenessUtils.ts)
 */

/** Captura un fotograma del video como dataURL (base64) para evidencia. */
export function capturarFoto(video: HTMLVideoElement): string {
  const canvas = document.createElement('canvas')
  canvas.width = video.videoWidth || 640
  canvas.height = video.videoHeight || 480
  const ctx = canvas.getContext('2d')
  if (ctx) ctx.drawImage(video, 0, 0, canvas.width, canvas.height)
  return canvas.toDataURL('image/jpeg', 0.7)
}

/** Detecta un identificador simple del dispositivo/navegador. */
export function detectarDispositivo(): string {
  const ua = navigator.userAgent
  const nav = /Chrome/.test(ua)
    ? 'Chrome'
    : /Firefox/.test(ua)
      ? 'Firefox'
      : /Safari/.test(ua)
        ? 'Safari'
        : 'Navegador'
  const so = /Windows/.test(ua)
    ? 'Windows'
    : /Mac/.test(ua)
      ? 'macOS'
      : /Android/.test(ua)
        ? 'Android'
        : /Linux/.test(ua)
          ? 'Linux'
          : 'SO'
  return `${nav}/${so}`
}
