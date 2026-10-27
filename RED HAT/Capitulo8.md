# Guía Definitiva y Completa de Vim

## 1. Introducción y Modos de Vim

Vim (*Vi IMproved*) es un editor de texto modal muy potente en Linux. Su característica principal es que el teclado se comporta de manera totalmente distinta según el modo en el que te encuentres:

* **Modo Command (o Normal):** Es el modo en el que abres el archivo por defecto. Sirve para navegar por el texto, eliminar, copiar, pegar y ejecutar órdenes del sistema. **Ninguna tecla que presiones escribirá texto en el archivo**.


* **Modo Insert (Inserción):** Permite escribir y editar texto libremente, como en cualquier editor moderno.


* **Modo Visual:** Permite seleccionar bloques de texto de forma precisa (por caracteres, líneas o columnas) para luego aplicarles operaciones (copiar, borrar, etc.).


* **Modo Command-Line (Extendido):** Se activa presionando dos puntos (`:`) desde el modo normal. Sirve para guardar, salir, buscar y configurar opciones del entorno.

---

## 2. Apertura de Archivos y Opciones Útiles desde la Terminal

Puedes abrir, crear o posicionar el cursor directamente en una línea específica al iniciar Vim usando los siguientes argumentos en la terminal:

* `vim archivo.txt` — Abre un archivo existente o crea uno nuevo si no existe.


* `vim +10 archivo.txt` — Abre el archivo y coloca automáticamente el cursor en la **línea 10**.
* `vim + archivo.txt` — Abre el archivo con el cursor en la **última línea**.
* `vimtutor` — Abre el tutor interactivo oficial de Vim en la terminal para practicar desde cero.



---

## 3. Transición entre Modos

* De cualquier modo $\rightarrow$ **Modo Command**: Presiona **`Esc`** (puedes presionarlo varias veces de forma segura si no estás seguro de en qué modo estás).
* De Modo Command $\rightarrow$ **Modo Insert**: Presiona **`i`**.


* De Modo Command $\rightarrow$ **Modo Visual**:
* `v` (Visual por caracteres).
* `Shift + v` (Visual por líneas enteras).
* `Ctrl + v` (Visual por bloques o columnas).


* De Modo Command $\rightarrow$ **Modo Extendido (`:`)**: Presiona **`:`**.

---

## 4. Comandos de Inserción de Texto (Entrar a Insert Mode)

Dependiendo de dónde quieras empezar a escribir respecto a la posición actual del cursor:

* `i` — Inserta texto **antes** del cursor.
* `a` — Inserta texto **después** del cursor (*append*).
* `I` — Inserta texto al **inicio de la línea actual**.
* `A` — Inserta texto al **final de la línea actual**.
* `o` — Abre una **nueva línea debajo** de la actual y entra en modo inserción.
* `O` — Abre una **nueva línea arriba** de la actual y entra en modo inserción.

---

## 5. Navegación Avanzada (Modo Command)

### Movimientos Básicos

* `h` $\rightarrow$ Izquierda
* `j` $\rightarrow$ Abajo
* `k` $\rightarrow$ Arriba
* `l` $\rightarrow$ Derecha
*(También puedes usar las flechas del teclado).*

### Movimiento por Palabras y Líneas

* `w` — Salta al inicio de la **siguiente palabra**.
* `b` — Retrocede al inicio de la **palabra anterior**.
* `0` (cero) o `Home` — Salta al **inicio absoluto** de la línea.
* `$` o `End` — Salta al **final absoluto** de la línea.

### Movimiento por Documento

* `gg` — Va a la **primera línea** del archivo.
* `G` — Va a la **última línea** del archivo.
* `:N` (ejemplo `:45`) — Salta directamente a la **línea N**.

---

## 6. Edición, Borrado, Copiado y Pegado (Yank & Put)

### Borrar y Cortar (`Delete`)

* `x` — Elimina el carácter bajo el cursor.
* `dw` — Elimina desde el cursor hasta el final de la palabra.
* `dd` — Corta/elimina la **línea completa**.
* `3dd` — Corta/elimina **3 líneas** consecutivas hacia abajo.
* `D` o `d$` — Elimina desde el cursor hasta el final de la línea.

### Copiar y Pegar (`Yank` y `Put`)

* `yy` (o `Y`) — Copia (*yank*) la **línea completa**.
* `3yy` — Copia **3 líneas** consecutivas.
* `p` — Pega (*put*) el contenido copiado/cortado **abajo** (o a la derecha) del cursor.
* `P` — Pega el contenido copiado/cortado **arriba** (o a la izquierda) del cursor.

### Deshacer y Rehacer

* `u` — Deshace la última acción (*undo*). Admite múltiples niveles de deshacer.


* `Ctrl + r` — Rehace la acción deshecha (*redo*).

---

## 7. Búsqueda y Reemplazo de Texto

* `/palabra` — Busca `palabra` hacia adelante en el archivo. Presiona `n` para ir a la siguiente coincidencia o `N` para la anterior.
* `?palabra` — Busca `palabra` hacia atrás en el archivo.
* `:%s/viejo/nuevo/g` — Reemplaza todas las apariciones de "viejo" por "nuevo" en todo el archivo de forma global.
* `:%s/viejo/nuevo/gc` — Reemplaza globalmente, pero **pidiendo confirmación** (`y`/`n`) en cada cambio.

---

## 8. Guardar y Salir (Modo Extendido `:`)

* `:w` — Guarda (*write*) los cambios sin salir.


* `:w nombre_nuevo.txt` — Guarda el archivo actual con un nombre nuevo (*Guardar como*).
* `:q` — Cierra el editor (falla si hay cambios sin guardar).


* `:wq` o `:x` — Guarda los cambios y cierra el editor al mismo tiempo.
* `:q!` — Cierra el editor **forzadamente descartando todos los cambios** no guardados.


* `ZZ` — Atajo en Modo Command para guardar y salir rápidamente.

---

## 9. Archivos de Configuración de Vim (`.vimrc`)

Puedes personalizar el comportamiento de Vim creando o editando un archivo de configuración (por ejemplo, para activar la numeración de líneas permanente o cambiar las tabulaciones).

* A nivel de usuario individual: `~/.vimrc`
* A nivel global del sistema: `/etc/vimrc`

**Ejemplo de contenido útil para un archivo `.vimrc`:**


set number          " Muestra los números de línea siempre
syntax on           " Activa el resaltado de sintaxis
set tabstop=4       " Define la tabulación a 4 espacios
set expandtab       " Convierte los tabs en espacios
