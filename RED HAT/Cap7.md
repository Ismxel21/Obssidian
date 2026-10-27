# 📁 Capítulo 7: Creación, Visualización y Edición de Archivos en RHEL

Notas completas de estudio para el **Capítulo 7 (Creación, Visualización y Edición de Archivos)** de **Red Hat Enterprise Linux (RHEL)**. Organizado y optimizado para ser añadido directamente a tu repositorio de **GitHub** en formato `.md`.

---

## 📋 Índice
1. [Organización y Jerarquía del Sistema de Archivos](#1-organización-y-jerarquía-del-sistema-de-archivos)
2. [Rutas Absolutas vs. Relativas](#2-rutas-absolutas-vs-relativas)
3. [Navegación y Comandos Esenciales](#3-navegación-y-comandos-esenciales)
4. [Expansiones de la Shell en Bash](#4-expansiones-de-la-shell-en-bash)
5. [Protección de Argumentos (Comillas y Escapes)](#5-protección-de-argumentos-comillas-y-escapes)
6. [Sustitución de Comandos y Variables](#6-sustitución-de-comandos-y-variables)
7. [Manipulación de Archivos y Enlaces (Hard & Soft Links)](#7-manipulación-de-archivos-y-enlaces-hard--soft-links)
8. [Laboratorio Resuelto: Review de Gestión de Archivos](#8-laboratorio-resuelto-review-de-gestión-de-archivos)

---

## 1. Organización y Jerarquía del Sistema de Archivos

RHEL utiliza un sistema de archivos unificado estructurado en forma de **árbol invertido**, donde la cúspide es el directorio raíz (`/`).

### Tabla de Directorios Principales (FHS)
| Directorio | Tipo de Contenido | Descripción / Propósito |
| :--- | :--- | :--- |
| `/` | Raíz | Directorio raíz que contiene todos los demás archivos y carpetas. |
| `/boot` | Estático | Archivos esenciales para el proceso de arranque del sistema. |
| `/dev` | Dinámico | Archivos especiales de dispositivos de hardware. |
| `/etc` | Estático / Persistente | Archivos de **configuración** del sistema. |
| `/home` | Persistente | Directorios personales de los usuarios regulares (`/home/usuario`). |
| `/root` | Persistente | Directorio personal exclusivo del administrador (`root`). |
| `/run` | Volátil | Datos del proceso de ejecución en tiempo real (se borra al reiniciar). |
| `/tmp` | Volátil | Espacio temporal de escritura pública (se borra automáticamente). |
| `/usr` | Estático | Binarios de programas, bibliotecas y documentación (`/usr/bin`, `/usr/sbin`). |
| `/var` | Dinámico / Persistente | Datos variables que persisten entre reinicios (logs, bases de datos, colas). |

> ⚠️ **Nota de Arquitectura RHEL 7+:**
> En versiones modernas de RHEL, `/bin`, `/sbin`, `/lib` y `/lib64` son **enlaces simbólicos** apuntando a sus equivalentes dentro de `/usr/` (ej. `/bin` $\rightarrow$ `/usr/bin`).

---

## 2. Rutas Absolutas vs. Relativas

### 📍 Ruta Absoluta (Absolute Path)
* Comienza **siempre** desde la raíz (`/`).
* Especifica la ubicación exacta e inequívoca de un archivo o carpeta en el sistema.
* **Ejemplo:** `/home/student/Documents/project-plans/season1-project-plan.odf`

### 📍 Ruta Relativa (Relative Path)
* Comienza desde el **directorio de trabajo actual** (no empieza con `/`).
* **Atajos especiales:**
  * `.` : El directorio actual.
  * `..` : El directorio padre (un nivel arriba).
  * `~` : El directorio *home* del usuario actual (`/home/student`).
  * `~usuario` : El directorio *home* de un usuario específico (ej. `~devops` $\rightarrow$ `/home/devops`).

---

## 3. Navegación y Comandos Esenciales

# Ver el directorio de trabajo actual
pwd

# Cambiar de directorio
cd /var/log                      # Ruta absoluta
cd ../Documents                  # Ruta relativa
cd ~                             # Ir al home del usuario
cd -                             # Volver al último directorio de trabajo previo

# Listar contenido
ls -l                            # Formato largo (detallado)
ls -a                            # Incluir archivos ocultos (inician con .)
ls -lh                           # Formato largo con tamaños leíbles (KB, MB)

# Crear directorios
mkdir mi_carpeta                 # Crear una carpeta
mkdir -p ruta/larga/de/carpetas   # Crear estructura multinivel automáticamente

# Crear archivos o actualizar marcas de tiempo
touch archivo.txt
touch archivo{1..5}.txt          # Crear múltiples archivos con expansiones

Aquí tienes el contenido desde la **sección 4 hasta la 8** redactado de forma limpia y estructurada, listo para que lo copies y pegues directamente en tus notas de **Obsidian**:

---

## 4. Expansiones de la Shell en Bash

Antes de ejecutar un comando, la shell Bash procesa la línea de comandos realizando varias expansiones de texto.

### A. Pattern Matching (Globbing / Comodines)

Permite seleccionar archivos mediante patrones y metacaracteres:

* `*` (Asterisco): Coincide con **cualquier cadena de cero o más caracteres**.
* `?` (Signo de interrogación): Coincide con **exactamente un carácter**.
* `[abc...]`: Coincide con cualquier carácter dentro de los corchetes.
* `[!abc...]` o `[^abc...]`: Coincide con cualquier carácter que **no** esté en el conjunto.
* **Clases de caracteres útiles:** `[[:alpha:]]` (letras), `[[:lower:]]` (minúsculas), `[[:upper:]]` (mayúsculas), `[[:digit:]]` (dígitos 0-9), `[[:alnum:]]` (alfanuméricos).

### B. Brace Expansion (Expansión de Llaves)

Genera cadenas discretas o secuencias utilizando listas separadas por comas o la sintaxis de doble punto (`..`):

* *Listas:* `echo {Sunday,Monday}.log` $\rightarrow$ `Sunday.log Monday.log`
* *Secuencias:* `echo file{1..3}.txt` $\rightarrow$ `file1.txt file2.txt file3.txt`
* *Uso práctico:* `mkdir -p ../RHEL{8,9,10}` para crear múltiples carpetas de golpe.

### C. Tilde Expansion y Otras Expansiones

* La tilde (`~`) se expande al directorio *home* del usuario actual (o de otro específico, ej. `~devops`).
* **Variable Expansion:** Sustituye `$VARIABLE` o `${VARIABLE}` por su valor almacenado.
* **Command Substitution:** `$(comando)` reemplaza la expresión por la salida estándar del comando ejecutado.

---

## 5. Protección de Argumentos (Comillas y Escapes)

Para evitar que la shell interprete caracteres especiales, se utilizan mecanismos de protección:

* **Barra invertida (`\`):** Actúa como carácter de escape protegiendo *únicamente* al carácter inmediatamente posterior para que se lea de forma literal.


* **Comillas dobles (`"`):** Detienen la mayoría de las expansiones (como el *globbing*), pero **permiten** la sustitución de variables (`$`), comandos (`$()`) y escapes con barra invertida.
* **Comillas simples (`'`):** Desactivan **absolutamente todas** las expansiones. Todo el contenido se interpreta de forma estrictamente literal.

---

## 6. Sustitución de Comandos y Variables

* **Variables:** Se asignan sin espacios alrededor del signo `=` (`NOMBRE=valor`) y se llaman anteponiendo un signo de dólar (`$NOMBRE`). Se recomienda usar llaves `${NOMBRE}` para evitar confusiones con texto adyacente.
* **Sustitución de comandos (`$(comando)`):** Permite usar el resultado de un comando dentro de otro.
* *Ejemplo:* `cp archivo.txt archivo-$(date +%F).txt`



---

## 7. Manipulación de Archivos y Enlaces (Hard & Soft Links)

* **Copiar, mover y borrar:**
* `cp`: Copia archivos o carpetas (con `-r` para recursivo).
* `mv`: Mueve o renombra archivos y directorios.
* `rm`: Elimina archivos (`rm -r` para directorios de forma recursiva).
* `rmdir`: Elimina directorios únicamente si están vacíos.


* **Enlaces Duros vs. Simbólicos:**
* *Enlace Duro:* Apunta directamente a los datos (mismo inodo). No cruza sistemas de archivos ni sirve para directorios.
* *Enlace Simbólico (`ln -s`):* Es un puntero/atajo a una ruta o nombre. Sí puede apuntar a directorios y cruzar sistemas de archivos; si se borra el original, queda un enlace colgante (*dangling symlink*).



---

## 8. Laboratorio Resuelto: Gestión y Revisión de Archivos

Flujo de comandos clave utilizados en las prácticas de gestión de archivos y directorios:


# Crear directorios y archivos usando expansiones de llaves
mkdir -p ~/Documents/project-plans
touch ~/Documents/project-plans/season{1,2}-project-plan.odf

# Crear múltiples archivos de prueba con patrones
touch tv-season{1..2}-episode{1..6}.ogg
touch mystery-chapter{1..8}.odf

# Organizar archivos en subdirectorios usando rutas relativas y comodines
mkdir -p ~/Videos/season{1,2}
mv tv-season1-*.ogg Videos/season1/
mv tv-season2-*.ogg Videos/season2/

# Uso de comandos de navegación y copias con patrones de corchetes
cd ~/Documents/my-bestseller
cp chapters/mystery-chapter[56].odf changes/

# Copia usando sustitución de comandos con fechas
cd changes
cp mystery-chapter5.odf mystery-chapter5-$(date +%F).odf

# Creación de enlaces duros
mkdir -p ~/Documents/links
ln ~/Documents/project-plans/season2-project-plan.odf ~/Documents/links/season2-project-plan.odf.link

```
