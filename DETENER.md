# ⏹️ Lynx · Cómo DETENER todo

> **© 2026 Abel Gomez. Todos los derechos reservados.**
>
> Hay dos partes que apagar: los **servicios locales** (Java + frontend) y la
> **infraestructura en Docker**.

---

## 1️⃣ Detener los servicios locales (Java + frontend)

### Opción A — cerrar las ventanas
Si los arrancaste cada uno en su terminal, simplemente pulsa **Ctrl + C** en
cada ventana (o ciérralas).

### Opción B — matar por puerto (Git Bash)
Mata lo que esté escuchando en los puertos de Lynx:

```bash
for p in 8761 8080 8081 8082 8084 8085 5173; do
  for pid in $(netstat -ano | grep ":$p" | grep LISTENING | awk '{print $5}' | sort -u); do
    taskkill //F //PID $pid
  done
done
```

### Opción B — matar por puerto (PowerShell)

```powershell
foreach ($p in 8761,8080,8081,8082,8084,8085,5173) {
  Get-NetTCPConnection -LocalPort $p -State Listen -ErrorAction SilentlyContinue |
    ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }
}
```

> ⚠️ Esto cierra cualquier proceso en esos puertos. Si usas alguno para otra
> cosa, ciérralos a mano en su lugar.

---

## 2️⃣ Detener la infraestructura (Docker)

### Parar pero conservar los datos
```bash
docker compose -f docker-compose.infra.yml stop
```
Vuelve a levantar con `docker compose -f docker-compose.infra.yml start`.

### Parar y eliminar los contenedores (conserva el volumen de datos)
```bash
docker compose -f docker-compose.infra.yml down
```
Los datos de PostgreSQL sobreviven en el volumen `lynx_lynx-pg-data`.

### Borrar TODO, incluidos los datos de la BD ⚠️
```bash
docker compose -f docker-compose.infra.yml down -v
```
> El flag `-v` **elimina el volumen** y borra usuarios, embeddings y logs.
> Úsalo solo si quieres empezar de cero.

---

## 3️⃣ Verificar que quedó todo apagado

```bash
# No debe quedar nada escuchando en los puertos de Lynx
netstat -ano | grep -E ":8761|:8080|:8081|:8082|:8084|:8085|:5173|:8083" | grep LISTENING

# No deben quedar contenedores lynx-*
docker ps | grep lynx
```

Si ambos comandos no devuelven nada, Lynx está completamente apagado.

---

Para volver a encender 👉 [LEVANTAR.md](LEVANTAR.md)

**© 2026 Abel Gomez. Todos los derechos reservados.**