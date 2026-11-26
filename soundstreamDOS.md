# Guía de Reinicio, Estado y Pendientes — SoundStream

Esta guía está pensada para cuando vuelvas a encender tus máquinas virtuales y retomes el proyecto **SoundStream** como **Integrante 4 (DevOps, Seguridad y Monitoreo)**, sin perder el hilo ni repetir trabajo ya validado.

---

## 1. En qué parte nos quedamos

* **Entorno base y repositorio:** El repositorio en `~/projects/soundstream` (en `server-soundstream` / VM1) está limpio y sincronizado con `origin/main` (`nothing to commit, working tree clean`).
* **Despliegue de servicios en VM1 (`server-soundstream`):** La automatización con Ansible se ejecutó sin errores (`failed=0`), con `sysadmin` en modo *rootless* (`linger` activo y `XDG_RUNTIME_DIR=/run/user/1000`), contenedores/unidades Quadlet levantados, PostgreSQL aceptando conexiones y Nginx respondiendo en `http://localhost:8443`.
* **Monitoreo con Munin en VM2 (`Monitoreo-soundstream`):** Está operativo con `munin.timer` y `httpd` activos, recolectando métricas de `server-soundstream` (`192.168.209.132`, puerto `4949` permitido solo desde `192.168.209.133`) y `localhost`, con panel accesible en `http://192.168.209.133/munin/`.
* **Base de datos (PostgreSQL 15 → 18.7):** Quedó intencionalmente sin modificar hasta que tu compañero encargado de base de datos suba sus cambios de migración a **PostgreSQL 18.7** al repositorio Git.
* **Estado de las VMs:** Ambas máquinas virtuales (`server-soundstream` y `Monitoreo-soundstream`) quedaron listas para apagarse limpiamente (`sudo shutdown -h now`).

---

## 2. Qué es todo lo que tenemos hasta ahora

### Infraestructura y redes
* **VM1 (`server-soundstream`):** AlmaLinux 9, usuario `sysadmin` con `sudo` y *linger* habilitado, SELinux en modo `Enforcing`, `firewalld` activo y Podman *rootless* con systemd Quadlet.
* **VM2 (`Monitoreo-soundstream`):** Servidor de monitoreo con Munin central (`munin.timer` / `munin-update` / `munin-graph` / `munin-html`) y servidor web Apache (`httpd`) sirviendo el dashboard en `/munin/`.
* **Red NAT (`192.168.209.0/24`) + Host-Only:** Permite salida a internet/actualizaciones, comunicación interna entre VM1 (`192.168.209.132`) y VM2 (`192.168.209.133`), y acceso local/SSH.

### Automatización, contenedores y monitoreo
* **Repositorio `~/projects/soundstream`:** Estructura de proyecto con `ansible/` (`inventory`, `playbook.yml` y roles como `podman_setup`, `security_hardening`, `database`, `streaming_app`, `nginx_proxy`, `monitoring`), archivos de contenedores/Quadlet (`.container`, `.network`, `.volume`), scripts/timers de respaldo (`soundstream-backup.timer` / `soundstream-backup.service`) y configuración de monitoreo.

---

## 3. Cómo inicializar cada cosa al volver a encender las VMs

1. **Encender las máquinas virtuales:** Inicia primero `server-soundstream` (VM1) y luego `Monitoreo-soundstream` (VM2) desde tu hipervisor. Verifica también que los servicios de red del hipervisor (como NAT/DHCP en VMware si aplica) estén activos.
2. **Revisar red y sincronizar repositorio en VM1 (`server-soundstream`):**
   ```bash
   ip addr show
   cd ~/projects/soundstream
   git status
   git pull origin main
   ```
3. **Levantar o asegurar servicios de usuario (Podman / Quadlet / respaldos) en VM1:**
   ```bash
   export XDG_RUNTIME_DIR=/run/user/1000
   systemctl --user daemon-reload
   systemctl --user start soundstream-db.service soundstream-nginx.service soundstream-backup.timer 2>/dev/null || true
   ```
4. **Asegurar servicios de monitoreo y web en VM2 (`Monitoreo-soundstream`):**
   ```bash
   sudo systemctl enable --now munin.timer httpd
   ```
5. **Evitar cambios prematuros en PostgreSQL:** No cambies manualmente la versión de PostgreSQL 15 a 18.7 hasta coordinarlo con el responsable de DB y revisar su commit en Git.

---

## 4. Cómo verificar que todo está levantado

### En VM2 (`Monitoreo-soundstream`)
```bash
systemctl is-active munin.timer httpd
curl -I -s http://127.0.0.1/munin/ | head -n 5
nc -zv 192.168.209.132 4949
sudo tail -n 10 /var/log/munin/munin-update.log /var/log/munin/munin-graph.log
```
* **Qué esperar:** `munin.timer` y `httpd` en `active`, respuesta HTTP válida en `/munin/`, puerto `4949` abierto hacia VM1 y logs recientes sin fallos críticos. Desde el navegador de tu equipo host abre `http://192.168.209.133/munin/`.

### En VM1 (`server-soundstream`)
```bash
sestatus
systemctl is-active firewalld munin-node
XDG_RUNTIME_DIR=/run/user/1000 systemctl --user status soundstream-db.service soundstream-nginx.service soundstream-backup.timer --no-pager
XDG_RUNTIME_DIR=/run/user/1000 podman ps
curl -I http://localhost:8443
XDG_RUNTIME_DIR=/run/user/1000 podman exec soundstream-db pg_isready -U soundstream_user -d soundstream
cd ~/projects/soundstream && git status -s
```
* **Qué esperar:** SELinux en `enforcing`, `firewalld` y `munin-node` activos, unidades de usuario corriendo, Nginx respondiendo en `8443`, PostgreSQL aceptando conexiones y el árbol de Git limpio.

---

## 5. Qué nos falta de todo para completar y finalizar

1. **Migración de base de datos a PostgreSQL 18.7:** Integrar el cambio de versión desde PostgreSQL 15 a `18.7` cuando el compañero de DB suba sus cambios a Git, validando datos, volúmenes y compatibilidad.
2. **Integración completa de aplicación (Backend Java/Tomcat + Frontend + Multimedia):** Conectar el `.war` / API de streaming con cabeceras `Range` (HTTP 206), la interfaz web del reproductor y el catálogo de audio libre de derechos (`.mp3` y carátulas) con volúmenes compartidos (`:z`) y privados (`:Z`) bien etiquetados.
3. **Publicación externa segura y TLS:** Completar la configuración del túnel/dominio (Cloudflare Tunnel y definición clara de terminación TLS/HTTPS) sin exponer puertos o servicios sensibles innecesarios.
4. **Higiene de secretos y hardening completo:** Verificar que contraseñas, tokens y claves estén cifrados con **Ansible Vault** (nada en texto plano en Git) y que el endurecimiento (SSH por clave, `PermitRootLogin no`, limpieza de servicios innecesarios como `cockpit` o `dhcpv6-client` si aplica) esté verificado.
5. **Matriz de pruebas (P01–P12), documentación y defensa:** Ejecutar las pruebas de extremo a extremo, completar los manuales técnicos/evidencias en PDF y preparar la demostración/exposición final.

---

## 6. Porcentaje de avance estimado del proyecto total

* **Avance estimado actual:** **~55 % – 60 %** del proyecto total.
  * **Qué ya está avanzado (~55%–60%):** Base del sistema operativo AlmaLinux 9, configuración de red NAT/Host-Only, SELinux y firewalld, Podman *rootless* con systemd Quadlet, estructura de roles/playbook en Ansible, despliegue inicial de servicios y monitoreo funcional con Munin.
  * **Qué falta (~40%–45%):** Migración a PostgreSQL 18.7 con los cambios del equipo de DB, integración final de backend/frontend/catálogo multimedia, túnel/HTTPS externo, blindaje final con Ansible Vault, ejecución completa de pruebas P01–P12 y entrega/documentación final.
