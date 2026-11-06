# Guía de Configuración: Lombok en Eclipse y Creación de Proyectos Gradle

---

## 1. Instalación de `lombok.jar` en Eclipse

### Paso 1: Descargar Lombok
- Entra a la página oficial de [Project Lombok](https://projectlombok.org/) y descarga el archivo `lombok.jar`.

### Paso 2: Cerrar Eclipse
- Asegúrate de cerrar Eclipse por completo para evitar errores al actualizar la configuración del IDE.

### Paso 3: Ejecutar el instalador
- Ejecuta el archivo descargado mediante doble clic o abre tu terminal en la ruta del archivo y ejecuta:
  ```bash
  java -jar lombok.jar
  ```

### Paso 4: Seleccionar el ejecutable de Eclipse e instalar
- El instalador buscará automáticamente la instalación de Eclipse.
- Si no aparece listada, haz clic en **"Specify location..."** y busca el archivo ejecutable de tu Eclipse (`eclipse.exe` o el binario correspondiente).
- Haz clic en **"Install / Update"**.

### Paso 5: Verificar la instalación
- Abre el archivo `eclipse.ini` ubicado en la carpeta principal de tu instalación de Eclipse.
- Confirma que al final del archivo aparezca una línea similar a la siguiente:
  ```text
  -javaagent:lombok.jar
  ```
- Vuelve a abrir Eclipse.

---

## 2. Recordatorio: Flujo de Creación de Proyectos Gradle

Sigue estos pasos específicos para registrar e integrar nuevos submódulos/proyectos Gradle en tu estructura:

1. **Crear la estructura de carpetas (PDS):**
   - Crea manualmente la ruta del proyecto dentro de la carpeta raíz:
     ```text
     [folder_name]/src/main/java
     ```

2. **Configurar el archivo de ajustes de Gradle:**
   - Abre el archivo `settings.gradle.kts`.
   - Agrega el nuevo proyecto a la configuración con la directiva `include`:
     ```kotlin
     include("folder_name")
     ```

3. **Copiar las configuraciones del proyecto base:**
   - Ubica la carpeta `lib` o el proyecto base generado.
   - Copia los archivos/bloques de `gradle.configurations` (o la configuración de dependencias y plugins necesaria) y pégalos dentro del directorio de tu nuevo proyecto (`folder_name`).

4. **Sincronizar y refrescar:**
   - En Eclipse, haz clic derecho sobre el proyecto raíz (o sobre la vista de Gradle).
   - Ejecuta la opción **Gradle -> Refresh Gradle Project**.

---

> **Nota adicional:** Si utilizas anotaciones de Lombok (`@Data`, `@Getter`, `@Setter`, etc.) dentro de tu proyecto Gradle, recuerda declarar la dependencia correspondiente en el archivo de construcción del proyecto (`build.gradle` o `build.gradle.kts`):
>
> ```kotlin
> compileOnly("org.projectlombok:lombok:1.18.30")
> annotationProcessor("org.projectlombok:lombok:1.18.30")
> ```
