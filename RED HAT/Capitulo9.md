# 📁 Capítulo 9: Redirección de Entradas y Salidas de la Shell

Notas completas de estudio para el **Capítulo 9 (Redirecting Shell Input and Output)** de **Red Hat Enterprise Linux (RHEL)**. Organizado y optimizado para ser añadido directamente a tu repositorio de **GitHub** o notas de **Obsidian** en formato `.md`.

---

## 📋 Índice
1. [Flujos Estándar y Descriptores de Archivo](#1-flujos-estándar-y-descriptores-de-archivo)
2. [Operadores de Redirección de Salida](#2-operadores-de-redirección-de-salida)
3. [El Dispositivo Especial `/dev/null`](#3-el-dispositivo-especial-devnull)
4. [Construcción de Tuberías (Pipelines)](#4-construcción-de-tuberías-pipelines)
5. [Tuberías Simultáneas con el Comando `tee`](#5-tuberías-simultáneas-con-el-comando-tee)
6. [Resumen de Comandos y Ejemplos Prácticos](#6-resumen-de-comandos-y-ejemplos-prácticos)
7. [Laboratorio Resuelto: Guided Exercise](#7-laboratorio-resuelto-guided-exercise)
8. [Cuestionario Repaso (Quiz)](#8-cuestionario-repaso-quiz)

---

## 1. Flujos Estándar y Descriptores de Archivo

Todo proceso en Linux interactúa con el usuario y el sistema a través de canales numerados llamados **Descriptores de Archivo (File Descriptors)**. Al iniciarse, un proceso cuenta con al menos tres canales por defecto:

### Tabla de Descriptores de Archivo (Channels)
| Número | Nombre del Canal | Abreviatura | Conexión por Defecto | Uso | Descripción |
| :---: | :--- | :---: | :--- | :---: | :--- |
| **0** | Standard Input | `stdin` | Teclado | Lectura | Entrada de datos al proceso. |
| **1** | Standard Output | `stdout` | Terminal | Escritura | Salida estándar de datos del proceso. |
| **2** | Standard Error | `stderr` | Terminal | Escritura | Mensajes de error generados por el proceso. |
| **3+** | Other Files | - | Ninguna | Lectura / Escritura | Archivos adicionales abiertos por el proceso. |

---

## 2. Operadores de Redirección de Salida

La redirección permite alterar los canales por defecto para que la salida de un proceso se guarde en un archivo o se combine con otros flujos.

| Operador | Nombre / Acción | Descripción |
| :--- | :--- | :--- |
| `> ARCHIVO` | Redirigir `stdout` (Sobrescribir) | Redirige la salida estándar a un archivo. Si el archivo no existe, lo crea; si existe, **sobrescribe** su contenido. |
| `>> ARCHIVO` | Redirigir `stdout` (Agregar / Append) | Redirige la salida estándar a un archivo. Si el archivo existe, **agrega** el contenido al final. |
| `2> ARCHIVO` | Redirigir `stderr` (Sobrescribir) | Redirige **únicamente los mensajes de error** a un archivo. |
| `2>> ARCHIVO` | Redirigir `stderr` (Agregar) | Agrega los mensajes de error al final de un archivo. |
| `> ARCHIVO 2>&1` | Redirigir `stdout` y `stderr` | Redirige la salida estándar a un archivo y luego redirige el error estándar al mismo destino. |
| `&> ARCHIVO` | Combinar `stdout` y `stderr` | Equivalente simplificado de `> ARCHIVO 2>&1`. Sobrescribe el archivo. |
| `&>> ARCHIVO` | Combinar y Agregar | Agrega tanto `stdout` como `stderr` al mismo archivo. |

> ⚠️ **Importante sobre el orden de redirección:**
> El orden en que se colocan los operadores importa. 
> * `> output.log 2>&1` : Primero dirige `stdout` a `output.log` y luego hace que `stderr` vaya a la misma ubicación (`stdout`). **(Correcto)**
> * `2>&1 > output.log` : Primero envía `stderr` a la terminal (`stdout` por defecto) y luego envía `stdout` al archivo. **(Incorrecto para guardar ambos)**

---

## 3. El Dispositivo Especial `/dev/null`

El archivo especial `/dev/null` actúa como un "agujero negro" del sistema de archivos. Cualquier dato enviado a este archivo se descarta de forma permanente.

```bash
# Descartar únicamente los mensajes de error de un comando
find /etc -name passwd 2> /dev/null

# Descartar la salida normal y guardar solo los errores
find /etc -name passwd > /dev/null 2> errores.txt

```

---

## 4. Construcción de Tuberías (Pipelines)

Una **tubería (*pipeline*)** se representa mediante el carácter `|`. Conecta la salida estándar (`stdout`) del comando de la izquierda a la entrada estándar (`stdin`) del comando de la derecha.

```
[ Comando 1 ] --- (stdout) ---> [ | ] ---> (stdin) ---> [ Comando 2 ]

```

* **Redirección vs. Pipelines:**
* Redirección (`>`, `<`) envía datos hacia o desde **archivos**.
* Tuberías (`|`) envían datos entre **procesos**.



### Redirección de Errores a través de un Pipeline

Para pasar tanto la salida estándar como el error a través de un pipeline hacia comandos como `less`:

```bash
find / -name passwd 2>&1 | less

```

---

## 5. Tuberías Simultáneas con el Comando `tee`

Si usas una redirección estándar (`>`) en medio de un pipeline, el flujo hacia los siguientes comandos se interrumpe. El comando `tee` resuelve esto actuando como una junta en "T": copia su entrada hacia la salida estándar (pantalla/pipeline) y a la vez la guarda en uno o más archivos.

```
                  ┌──> Guardar en Archivo
[ stdin ] ──> [ tee ]
                  └──> stdout (Terminal / Siguiente comando)

```

```bash
# Redirigir a un archivo y continuar con el pipeline hacia less
ls -l | tee /tmp/saved-output | less

# Mostrar el resultado en pantalla y guardar en archivo al final del pipeline
ls -t | head -n 10 | tee /tmp/ten-last-changed-files

# Agregar datos a un archivo existente sin sobrescribir (-a)
ls -l | tee -a /tmp/append-files

```

---

## 6. Resumen de Comandos y Ejemplos Prácticos

```bash
# Save a time stamp in a file
date > /tmp/saved-timestamp

# Copy the last 100 lines from a log file
tail -n 100 /var/log/secure > /tmp/last-100-log-secure

# Concatenate multiple files into one
cat step1.sh step2.log step3 step4 > /tmp/all-four-steps-in-one

# List directory contents including hidden files and save
ls -a > my-file-names

# Append a line to an existing file
echo "new line of information" >> /tmp/many-lines-of-information

# Separate output and errors into different files
find /etc -name passwd > /tmp/output 2> /tmp/errors

# Store output and errors together
find /etc -name passwd &> /tmp/all-message-output

```

---

## 7. Laboratorio Resuelto: Guided Exercise

### Escenario

Investigación de fallas en la aplicación `my_web` cuyos registros están ubicados en `/var/opt/my_web/`.

```bash
# 1. Iniciar laboratorio
lab start shellio-redirect

# 2. Extraer las primeras 2 líneas (encabezados) de todos los archivos de registro
head -n 2 /var/opt/my_web/* > headers.txt

# 3. Extraer las últimas 5 líneas (pie de página) de todos los archivos de registro
tail -n 5 /var/opt/my_web/* > footers.txt

# 4. Registrar la identificación del archivo con errores
echo "Log file with errors: 20250507.log" > ~/findings.txt

# 5. Buscar archivos .conf en /etc separando resultados y errores
find /etc -name "*.conf" > results.txt 2> search_errors.txt

# 6. Contar líneas de resultados y errores, agregándolos a findings.txt y mostrándolos en pantalla
wc -l results.txt | tee -a findings.txt
wc -l search_errors.txt | tee -a findings.txt

# 7. Agregar la fecha actual al archivo de hallazgos
date >> findings.txt

# 8. Verificar el archivo final
cat findings.txt

# 9. Finalizar laboratorio
lab finish shellio-redirect

```

---

## 8. Cuestionario Repaso (Quiz)

### Q1: Which number is assigned to the standard output (stdout) channel?

* **Respuesta:** **B) 1**
* *Explicación:* `0` es `stdin`, `1` es `stdout`, `2` es `stderr`.

### Q2: Which shell redirection operator should you use to add output to the end of a file without removing content?

* **Respuesta:** **D) >>**
* *Explicación:* `>>` realiza una operación de agregado (*append*).

### Q3: Which command should you use to discard the standard error?

* **Respuesta:** **D) 2> /dev/null**
* *Explicación:* El canal `2` redirigido a `/dev/null` descarta los mensajes de error.

### Q4: Which command syntax saves stdout to output.txt and stderr to errors.txt?

* **Respuesta:** **C) command > output.txt 2> errors.txt**
* *Explicación:* Permite separar ambos flujos en archivos independientes.

### Q5: What is the purpose of the pipe character (`|`)?

* **Respuesta:** **D) It connects the standard output of command1 to the standard input of command2.**

### Q6: Which command appends to find to save stdout and stderr to a single file overwriting it?

* **Respuesta:** **D) > find-results-and-errors.log 2>&1**

### Q7: Consider `commandA > file.txt | commandB`. What happens to the stdout of `commandA`?

* **Respuesta:** **C) The output is redirected to the file instead of being sent to commandB.**

### Q8: Which command enables you to send to both standard output and to one or more files?

* **Respuesta:** **D) tee**

### Q9: What is the result of `ls -t | head -n 10 | tee -a my-list.txt`?

* **Respuesta:** **B) The first 10 files sorted by modification time are displayed on the terminal, and the list is appended to the my-list.txt file.**

```

```
