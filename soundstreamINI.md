# Guía Técnica de Despliegue y Reproducción desde Cero — SoundStream

**Proyecto:** Plataforma de Streaming Multimedia Empresarial (`SoundStream` / "Mini-Spotify")  
**Sistema Operativo:** AlmaLinux 9 (`x86_64`)  
**Stack de Infraestructura:** Podman Rootless + Systemd Quadlet + Ansible + SELinux (`Enforcing`) + `firewalld` + Munin  
**Usuario de Administración y Contenedores:** `sysadmin` (UID `1000`)  

---

## Parte 1: Resumen de lo Realizado y Solucionado en el Despliegue

Durante la configuración y puesta en marcha de la infraestructura en `server-soundstream` (VM1) y la preparación de `Monitoreo-soundstream` (VM2), resolvimos los puntos críticos de **Podman Rootless**, **Systemd Quadlet**, **Ansible** y **Munin**:

### 1. Persistencia de Sesión Rootless (`systemd --user` y D-Bus)
- **Problema inicial:** Al ejecutar tareas de `ansible.builtin.systemd` con `scope: user`, Ansible fallaba con `Failed to connect to bus` porque la sesión de usuario no tenía acceso al bus de D-Bus cuando se escalaba privilegios o se ejecutaba de forma desatendida.
- **Solución:**
  - Habilitar *linger* para el usuario administrador (`loginctl enable-linger sysadmin`) para que sus servicios y contenedores permanezcan activos sin sesión interactiva.
  - Definir explícitamente en cada tarea `scope: user` de Ansible:
    ```yaml
    become: true
    become_user: sysadmin
    environment:
      XDG_RUNTIME_DIR: "/run/user/1000"
    ```

### 2. Orden de Despliegue y Generación de Unidades Quadlet (`database`)
- **Problema inicial:**
  - Error de sintaxis/sangría en el `loop:` de `ansible/roles/database/tasks/main.yml`.
  - Error `Could not find the requested service soundstream-db.service: host` porque `systemd` intentaba arrancar el servicio antes de desplegar los archivos `.container`, `.network` y `.volume` en `~/.config/containers/systemd/`.
  - Error de ejecución de Podman (`exit status 125`) al arrancar `soundstream-db.service` por faltar en disco los archivos montados/referenciados (`/home/sysadmin/.config/containers/systemd/postgres.env` y `/home/sysadmin/soundstream-deploy/database/init.sql`).
- **Solución:**
  - Estructurar `ansible/roles/database/tasks/main.yml` para crear los directorios de usuario, copiar `init.sql` y las unidades de respaldo (`soundstream-backup.service` y `soundstream-backup.timer`), y renderizar todas las plantillas (`soundstream-net.network.j2`, `soundstream-pgdata.volume.j2`, `postgres.env.j2`, `backup_db.sh.j2` y `soundstream-db.container.j2`) **antes** de ejecutar `daemon_reload: true` e iniciar `soundstream-db.service`.

### 3. Permisos de Volúmenes y Configuración en Nginx (`nginx_proxy`)
- **Problema inicial:** `soundstream-nginx.service` fallaba al iniciar con `exit status 126` debido a que el directorio `/home/sysadmin/soundstream-deploy/nginx` y el archivo `nginx.conf` pertenecían a `root:root`, impidiendo su lectura/etiquetado `:ro,Z` desde el contenedor rootless de `sysadmin`.
- **Solución:**
  - Asignar propiedad `sysadmin:sysadmin` sobre `/home/sysadmin/soundstream-deploy/nginx` y `/var/lib/soundstream/media`.
  - Verificar que el proxy inverso publique el puerto `8443:80` sin requerir privilegios de `root` (< 1024).

### 4. Verificación Final de Servicios en VM1 y Avance en VM2
- **Ansible Playbook (VM1):** Ejecutado de extremo a extremo con `ok=23 changed=1 failed=0`.
- **PostgreSQL (`soundstream-db`):** Activo en `soundstream-net`, validado con `pg_isready -U soundstream_user -d soundstream` (`accepting connections`).
- **Nginx (`soundstream-nginx`):** Activo (`active (running)`), validado con `curl -I http://localhost:8443` (`HTTP/1.1 200 OK`).
- **Monitoreo (`Monitoreo-soundstream` / VM2):** Instalada en AlmaLinux 9 con usuario `sysadmin`, repositorios EPEL/CRB habilitados y paquete `munin` disponible para configurar como servidor central de monitoreo.

---

## Parte 2: Guía Paso a Paso para Reproducir Todo desde la Instalación de AlmaLinux 9

Sigue estos pasos si necesitas recrear las máquinas virtuales o desplegar el entorno desde cero.

### Paso 1: Creación de las Máquinas Virtuales (VM1 y VM2)
1. Descargar la ISO oficial de **AlmaLinux 9** (`Minimal ISO` o `DVD ISO`, arquitectura `x86_64`) desde [almalinux.org](https://almalinux.org/get-almalinux/).
2. **VM1 (`server-soundstream.local` — Aplicación, BD, Proxy y `munin-node`):**
   - **Sistema Operativo:** Linux / Red Hat (64-bit)
   - **RAM:** `4096 MB` (4 GB)
   - **CPU:** `2 vCPU`
   - **Disco Duro:** `30 GB` (dinámico)
   - **Adaptador 1:** `NAT` (salida a Internet para descargar paquetes e imágenes).
   - **Adaptador 2:** `Adaptador Solo-anfitrión (Host-Only)` (red interna con el host y VM2, ej. `192.168.56.10`).
3. **VM2 (`Monitoreo-soundstream` — Munin Central):**
   - **Sistema Operativo:** Linux / Red Hat (64-bit)
   - **RAM:** `2048 MB` (2 GB)
   - **CPU:** `1–2 vCPU`
   - **Disco Duro:** `15 GB – 20 GB` (dinámico)
   - **Adaptador 1:** `NAT` (salida a Internet para repositorios y EPEL).
   - **Adaptador 2:** `Adaptador Solo-anfitrión (Host-Only)` (comunicación interna con VM1 y el host).

### Paso 2: Instalación Base de AlmaLinux 9
1. Arrancar la VM con la ISO montada y seleccionar **Install AlmaLinux 9**.
2. Seleccionar idioma (Español / English).
3. En **Destino de la instalación**, seleccionar el disco y dejar el particionado automático (LVM).
4. En **Red y nombre de host**, activar ambas tarjetas de red y establecer el hostname correspondiente (`server-soundstream.local` en VM1 y `Monitoreo-soundstream` o similar en VM2).
5. En **Ajustes de usuario**:
   - Definir contraseña para `root`.
   - Crear el usuario `sysadmin` (UID `1000`) y marcar **"Hacer que este usuario sea administrador"** (`wheel` / `sudo`).
6. Finalizar instalación, expulsar la ISO y reiniciar.

### Paso 3: Preparación Post-Instalación del Servidor
Iniciar sesión como `sysadmin` (por consola o vía `ssh sysadmin@<IP_HOST_ONLY>`) y ejecutar:

```bash
# 1. Actualizar el sistema base
sudo dnf update -y

# 2. Verificar IP asignada y estado de SELinux (debe ser Enforcing)
ip addr show
sestatus

# 3. Instalar EPEL, Podman, herramientas de red rootless, Git y Ansible
sudo dnf install -y epel-release
sudo dnf install -y podman podman-docker crun slirp4netns container-selinux git ansible-core

# 4. Instalar colecciones necesarias de Ansible
ansible-galaxy collection install ansible.posix containers.podman

# 5. Habilitar linger para el usuario sysadmin (persistencia de contenedores rootless)
sudo loginctl enable-linger sysadmin
```

### Paso 4: Configuración Básica de Seguridad (`firewalld` y SELinux)
Asegurar que `SELinux` permanezca en `enforcing` y habilitar el cortafuegos con los puertos necesarios:

```bash
sudo systemctl enable --now firewalld
sudo firewall-cmd --permanent --add-service=ssh
sudo firewall-cmd --permanent --add-service=https
sudo firewall-cmd --permanent --add-port=8443/tcp
sudo firewall-cmd --permanent --remove-service=cockpit || true
sudo firewall-cmd --permanent --remove-service=dhcpv6-client || true
sudo firewall-cmd --reload
sudo firewall-cmd --list-all
```

*(Para permitir el puerto `4949/tcp` de Munin en **VM1** únicamente desde la IP Host-Only de **VM2**, ver la sección dedicada a VM2 y Munin más abajo).*

### Paso 5: Clonar el Repositorio del Proyecto
Descargar el código e infraestructura como código en el servidor:

```bash
mkdir -p ~/projects
cd ~/projects
git clone https://github.com/Ismxel21/soundstream-project.git soundstream
cd soundstream
```

### Paso 6: Estructura y Archivo Clave del Rol `database`
Asegúrate de que `ansible/roles/database/tasks/main.yml` contenga las tareas en el orden correcto (primero archivos/plantillas, luego `systemd --user` con `XDG_RUNTIME_DIR`):

```yaml
---
- name: Asegurar directorios para Quadlet, systemd de usuario y base de datos
  ansible.builtin.file:
    path: "{{ item }}"
    state: directory
    owner: sysadmin
    group: sysadmin
    mode: '0755'
  loop:
    - /home/sysadmin/.config/containers/systemd
    - /home/sysadmin/.config/systemd/user
    - /home/sysadmin/soundstream-deploy/db
    - /home/sysadmin/soundstream-deploy/database

- name: Copiar init.sql y unidades systemd de respaldo
  ansible.builtin.copy:
    src: "{{ item.src }}"
    dest: "{{ item.dest }}"
    owner: sysadmin
    group: sysadmin
    mode: '0644'
  loop:
    - { src: "init.sql", dest: "/home/sysadmin/soundstream-deploy/database/init.sql" }
    - { src: "soundstream-backup.service", dest: "/home/sysadmin/.config/systemd/user/soundstream-backup.service" }
    - { src: "soundstream-backup.timer", dest: "/home/sysadmin/.config/systemd/user/soundstream-backup.timer" }

- name: Desplegar plantillas de red, volumen, entorno, respaldo y Quadlet PostgreSQL
  ansible.builtin.template:
    src: "{{ item.src }}"
    dest: "{{ item.dest }}"
    owner: sysadmin
    group: sysadmin
    mode: "{{ item.mode }}"
  loop:
    - { src: "soundstream-net.network.j2", dest: "/home/sysadmin/.config/containers/systemd/soundstream-net.network", mode: "0644" }
    - { src: "soundstream-pgdata.volume.j2", dest: "/home/sysadmin/.config/containers/systemd/soundstream-pgdata.volume", mode: "0644" }
    - { src: "postgres.env.j2", dest: "/home/sysadmin/.config/containers/systemd/postgres.env", mode: "0600" }
    - { src: "backup_db.sh.j2", dest: "/home/sysadmin/soundstream-deploy/db/backup_db.sh", mode: "0755" }
    - { src: "soundstream-db.container.j2", dest: "/home/sysadmin/.config/containers/systemd/soundstream-db.container", mode: "0644" }

- name: Recargar systemd de usuario e iniciar base de datos PostgreSQL
  ansible.builtin.systemd:
    name: soundstream-db.service
    state: started
    daemon_reload: true
    scope: user
  become: true
  become_user: sysadmin
  environment:
    XDG_RUNTIME_DIR: "/run/user/1000"

- name: Habilitar e iniciar temporizador diario de respaldos
  ansible.builtin.systemd:
    name: soundstream-backup.timer
    state: started
    enabled: true
    daemon_reload: true
    scope: user
  become: true
  become_user: sysadmin
  environment:
    XDG_RUNTIME_DIR: "/run/user/1000"
```

> **Nota para `nginx_proxy`:** Verifica que todos los directorios y plantillas de Nginx (`/home/sysadmin/soundstream-deploy/nginx` y `/var/lib/soundstream/media`) tengan como propietario `sysadmin:sysadmin` antes de iniciar `soundstream-nginx.service`.

### Paso 7: Validar y Ejecutar el Playbook de Ansible
Desde la raíz del repositorio (`~/projects/soundstream`):

```bash
# 1. Validar sintaxis
ansible-playbook --syntax-check -i ansible/inventory ansible/playbook.yml

# 2. Ejecutar el despliegue completo
ansible-playbook -i ansible/inventory ansible/playbook.yml -K
```

### Paso 8: Comprobaciones Finales de Operación en VM1
Ejecutar las verificaciones de estado y conectividad para confirmar que toda la plataforma está operativa:

```bash
# Estado de unidades systemd de usuario (Quadlet y timers)
XDG_RUNTIME_DIR=/run/user/1000 systemctl --user status soundstream-db.service soundstream-nginx.service soundstream-backup.timer --no-pager

# Contenedores activos en Podman rootless
XDG_RUNTIME_DIR=/run/user/1000 podman ps

# Salud de PostgreSQL
XDG_RUNTIME_DIR=/run/user/1000 podman exec soundstream-db pg_isready -U soundstream_user -d soundstream

# Respuesta HTTP del proxy inverso Nginx
curl -I http://localhost:8443
```

---

## Parte 3: Configuración y Operación de VM2 (`Monitoreo-soundstream` — Munin Central)

Esta sección documenta lo que se realiza en **VM2** para el rol de monitoreo independiente y los entregables del equipo.

### 1. Base de VM2 y Paquetes de Munin
1. **Verificación de red y seguridad en VM2:**
   - Confirmar doble interfaz (**NAT** + **Host-Only**) con `ip addr show` y anotar la IP Host-Only real de VM2 (`<IP_HOST_ONLY_VM2>`).
   - Confirmar `SELinux` en `Enforcing` (`sestatus`) y `firewalld` activo (`sudo systemctl enable --now firewalld`).
2. **Habilitar repositorios e instalar Munin central:**
   ```bash
   sudo dnf install -y epel-release dnf-plugins-core
   sudo dnf config-manager --set-enabled crb
   sudo dnf makecache
   dnf search munin
   sudo dnf install -y munin
   ```

### 2. Regla de Firewall en VM1 para el Puerto `4949/tcp`
En **VM1** (`server-soundstream.local`), permitir el puerto `4949/tcp` (`munin-node`) **únicamente** desde la IP Host-Only de **VM2** mediante una regla *rich* de `firewalld`:

```bash
# Ejecutar en VM1 (reemplazar <IP_HOST_ONLY_VM2> por la IP Host-Only real de VM2)
sudo firewall-cmd --permanent --add-rich-rule='rule family="ipv4" source address="<IP_HOST_ONLY_VM2>/32" port protocol="tcp" port="4949" accept'
sudo firewall-cmd --reload
sudo firewall-cmd --list-rich-rules
```

### 3. Configuración de Munin Central en VM2 y Conexión con VM1
1. Registrar en `/etc/munin/munin.conf` (en **VM2**) el nodo de **VM1** apuntando a su IP interna y al puerto `4949`, verificando también los umbrales de alertas (`warning` / `critical`) y plugins necesarios para las métricas y pruebas de monitoreo.
2. Verificar conectividad de red y alcance al puerto `4949` desde **VM2** hacia **VM1** (`<IP_VM1>`):
   ```bash
   ping -c 2 <IP_VM1>
   nc -zv <IP_VM1> 4949 || timeout 3 bash -c "</dev/tcp/<IP_VM1>/4949" && echo "Puerto 4949 accesible"
   ```
3. Actualizar el inventario de Ansible (`ansible/inventory`) para incluir tanto **VM1** como **VM2** con sus IPs reales y usuario `sysadmin`, y validar con `ansible all -i ansible/inventory -m ping`.

### 4. Entregables del Equipo Cubiertos con VM1 y VM2
- **Tabla de VM1 y VM2:** SO (AlmaLinux 9), IPs (NAT y Host-Only), hostnames, RAM, CPU, redes y reglas de `firewalld` (`4949/tcp` solo desde la IP de VM2).
- **Inventario inicial y estructura de roles de Ansible:** Grupos diferenciados para los nodos/roles y estructura modular bajo `ansible/roles/`.
- **Monitoreo (`Munin`):** VM2 como servidor central recolectando métricas de VM1 (`munin-node` en `4949/tcp`) con gráficos y alertas.
- **Acta de reparto y diagrama general de arquitectura:** Documentados en coordinación con los 4 integrantes del equipo.

---

## Parte 4: Checklist de Entregables Pendientes del Proyecto

Antes de la entrega final o defensa del proyecto, revisa con tu equipo estos puntos adicionales del documento de arquitectura:

- [ ] **Integración de Backend (`streaming_app`):** Verificar que el contenedor Java/Tomcat con el `.war` de la API REST esté conectado a `soundstream-net` y responda correctamente detrás de Nginx.
- [ ] **Publicación Externa (`cloudflared`):** Configurar el servicio systemd de Cloudflare Tunnel apuntando al proxy local (`:8443` / servicio interno) para salida pública con HTTPS sin exponer puertos internos innecesarios.
- [ ] **Monitoreo Independiente (`Munin`):** Completar la configuración de Munin central en VM2 hacia `munin-node` en VM1 (`4949/tcp` restringido por regla *rich* en `firewalld`), verificando métricas de CPU, RAM, disco, red y alertas.
- [ ] **Gestión de Secretos (`Ansible Vault`):** Cifrar contraseñas de base de datos y tokens antes de subir cambios finales a GitHub.
