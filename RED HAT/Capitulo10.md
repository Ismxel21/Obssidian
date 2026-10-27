Voy a generar una guía completa de referencia y repaso en formato Markdown que consolide y organice de forma clara todos los temas de administración de Red Hat Enterprise Linux (RHEL) incluidos en tus apuntes.

```markdown:Guia_Administracion_RHEL.md
# Guía Completa de Administración de Red Hat Enterprise Linux (RHEL)

## 1. Cuentas de Usuario y Propiedad de Archivos

### Identificadores (UID y GID)
Linux gestiona la seguridad y el acceso a recursos mediante identificadores numéricos únicos:
* **UID (User ID):** Identificador numérico de usuario.
  * `0`: Reservado exclusivamente para la cuenta administrativa **root**.
  * `1` - `999`: Reservado para **usuarios del sistema** (ejecución de servicios y demonios sin shell interactiva).
  * `1000+`: Asignado a **usuarios regulares**.
* **GID (Group ID):** Identificador numérico del grupo.
  * **Grupo Principal (Primary Group):** Creado automáticamente junto con el usuario con el mismo nombre y GID idéntico al UID.
  * **Grupos Secundarios/Complementarios:** Grupos adicionales a los que el usuario puede ser asignado para otorgar accesos específicos.

### Comandos de Inspección
```bash
# Muestra UID, GID y todos los grupos asignados al usuario actual
id

# Muestra la información completa de un usuario específico
id developer1

# Muestra únicamente el GID del grupo principal (numérico)
id -g developer1

# Muestra el nombre del grupo principal
id -g --name developer1

# Lista todos los grupos a los que pertenece un usuario
groups developer1

```

### Gestión de Propiedad (`chown`)

El comando `chown` modifica el usuario propietario y/o el grupo propietario de archivos y directorios:

```bash
# Cambiar solo el propietario
sudo chown usuario archivo.txt

# Cambiar solo el grupo propietario (requiere los dos puntos ":")
sudo chown :grupo_devs archivo.txt

# Cambiar usuario y grupo simultáneamente
sudo chown developer1:java-devs /Projects/Java-code

# Aplicar cambios de forma recursiva a un directorio y su contenido
sudo chown -R operator1:finance /Projects/Artifacts

```

### Elevación de Privilegios (`sudo`)

* **Propósito:** Permitir a usuarios autorizados ejecutar comandos administrativos suplantando a `root` de forma auditada.
* **Comandos:**

```bash
  # Ejecutar un comando con privilegios administrativos
  sudo chown :sales-team /Finance/Sales

  # Listar los privilegios sudo permitidos para el usuario actual
  sudo -l
  

```

---

## 2. Permisos de Archivos y Directorios

### Permisos Estándar (rwx)

Los permisos determinan las acciones permitidas para tres conjuntos de identidades: **Usuario (u)**, **Grupo (g)** y **Otros (o)**.

| Permiso | Letra | Octal | Significado en Archivos | Significado en Directorios |
| --- | --- | --- | --- | --- |
| **Read** | `r` | `4` | Leer/ver el contenido del archivo | Listar el contenido del directorio (`ls`) |
| **Write** | `w` | `2` | Modificar el contenido del archivo | Crear, mover o borrar archivos dentro del directorio |
| **Execute** | `x` | `1` | Ejecutar el archivo como programa/script | Entrar/navegar en el directorio (`cd`) |

### Notación Octal y Conversión

$$\text{Valor Octal} = \text{Read}(4) + \text{Write}(2) + \text{Execute}(1)$$

* `7` (`4+2+1`): `rwx` (Acceso total)
* `6` (`4+2+0`): `rw-` (Lectura y escritura)
* `5` (`4+0+1`): `r-x` (Lectura y ejecución)
* `4` (`4+0+0`): `r--` (Solo lectura)
* `0` (`0+0+0`): `---` (Sin permisos)

*Ejemplo:* `0750` representa permisos especiales `0`, usuario propietario `7` (`rwx`), grupo propietario `5` (`r-x`) y otros `0` (`---`).

### Modificación de Permisos (`chmod`)

```bash
# Modo Octal: Asigna permisos rwxr-x--- (0750)
chmod 0750 daily-tasks.sh

# Modo Simbólico: Añadir ejecución al propietario
chmod u+x script.sh

# Modo Simbólico: Quitar escritura al grupo
chmod g-w documento.docx

# Aplicación recursiva a directorios
chmod -R 755 /var/www/html

```

---

## 3. Jerarquía del Sistema de Archivos en RHEL

El sistema de archivos de Linux se organiza en una estructura de árbol invertido que inicia en el **directorio raíz (`/`)**.

```
/ (Root Directory)
├── boot/       (Archivos de arranque del Kernel y GRUB)
├── dev/        (Archivos de dispositivos de hardware)
├── etc/        (Archivos de configuración del sistema)
├── home/       (Directorios personales de usuarios estándar)
├── root/       (Directorio personal del superusuario root)
├── run/        (Datos de ejecución del sistema y procesos activos)
├── tmp/        (Archivos temporales, limpieza automática a los 10 días)
├── usr/        (Programas, comandos y bibliotecas de solo lectura)
│   ├── bin -> /bin
│   └── sbin -> /sbin
└── var/        (Datos variables: logs, colas de impresión, bases de datos)
    └── tmp/    (Archivos temporales persistentes, limpieza a los 30 días)

```

### Clasificación de Contenido

* **Estático:** No cambia sin intervención explícita (ej. `/usr`, `/boot`).
* **Variable/Dinámico:** Modificado por procesos activos (ej. `/var/log`).
* **Persistente:** Almacenamiento permanente entre reinicios.
* **Tiempo de ejecución (Runtime):** Volátil, se regenera al iniciar (ej. `/run`, `/proc`).

### Enlaces Simbólicos Modernos en RHEL

Desde RHEL 7+, los directorios tradicionales en la raíz corresponden a **enlaces simbólicos (symlinks)** apuntando hacia `/usr`:

* `/bin` $\rightarrow$ `/usr/bin`
* `/sbin` $\rightarrow$ `/usr/sbin`
* `/lib` $\rightarrow$ `/usr/lib`
* `/lib64` $\rightarrow$ `/usr/lib64`

---

## 4. Manipulación de Archivos y Directorios

### Navegación y Ubicación

```bash
# Mostrar ruta absoluta del directorio actual
pwd

# Cambiar de directorio
cd /var/log           # Ruta absoluta
cd ../Documents       # Ruta relativa
cd ~                  # Ir al directorio home del usuario
cd -                  # Volver al directorio de trabajo previo

```

### Creación

```bash
# Crear archivos vacíos o actualizar timestamps
touch archivo.txt

# Crear directorios
mkdir ProyectoA ProyectoB

# Crear estructura de directorios anidados (crea carpetas padre si no existen)
mkdir -p Thesis/Chapter1/Notes

```

### Copia, Mapeo y Eliminación

```bash
# Copiar archivos
cp origen.txt destino.txt
cp file1.txt file2.txt /ruta/destino/

# Copiar directorios recursivamente
cp -r /origen/carpeta /destino/

# Mover o renombrar
mv viejo_nombre.txt nuevo_nombre.txt
mv -v archivo.txt /tmp/

# Eliminar archivos (irreversible en CLI)
rm archivo.txt

# Eliminar directorios no vacíos y su contenido
rm -r /directorio/

# Eliminar interactivamente (pide confirmación)
rm -ri /directorio_sensible/

# Eliminar directorios completamente vacíos
rmdir directorio_vacio

```

### Identificación de Archivos

Linux no depende de las extensiones de archivo para determinar su tipo, sino del **encabezado (número mágico)** analizado por la herramienta `file`:

```bash
file /bin/cat
# Salida: ELF 64-bit LSB shared object (Binario ejecutable)

file documento.docx
# Salida: Microsoft Word 2007+

file .bashrc
# Salida: ASCII text

```

---

## 5. Accesos al Sistema e Interfaces

### Entornos de Interacción

1. **Consolas Virtuales (TTY):**
* Transición entre consolas de texto y gráficas mediante `Ctrl + Alt + F1` hasta `F6`.
* En RHEL, `tty1` gestiona la pantalla de inicio de sesión gráfico (GDM), mientras que la sesión activa se ejecuta habitualmente en `tty2`.


2. **Interfaz Gráfica GNOME:** Administrada a través de GDM.
3. **Consola Web Cockpit:**
* Servicio de administración remota vía navegador en `https://localhost:9090` (puerto 9090).
* Permite gestión de usuarios, servicios, redes y almacenamiento mediante módulos gráficos.



### Comandos Generales de Información

```bash
# Muestra el nombre de red de la máquina
hostname
hostname -s   # Nombre corto
hostname -d   # Nombre de dominio

# Identificación de la hora y estado de sincronización NTP
date
timedatectl

# Listado detallado de archivos (ordenado por fecha de modificación y tamaño legible)
ls -lhS
ls -la   # Incluye archivos ocultos (los que inician con .)

```

```eof

```
